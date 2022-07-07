package com.sdk;

import com.couchbase.client.core.deps.io.netty.util.ResourceLeakDetector;
import com.couchbase.client.core.error.BucketExistsException;
import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.StorageBackend;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sdk.config.BuiltSdkCommand;
import com.sdk.config.Op;
import com.sdk.config.OpGet;
import com.sdk.config.OpInsert;
import com.sdk.config.OpRemove;
import com.sdk.config.OpReplace;
import com.sdk.config.TestSuite;
import com.sdk.constants.Defaults;
import com.sdk.constants.Strings;
import com.couchbase.grpc.sdk.protocol.*;
import com.sdk.sdk.util.DbWriteThread;
import com.sdk.sdk.util.DocCreateThread;
import com.sdk.sdk.util.Performer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.okhttp3.Credentials;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.sdk.sdk.util.DbWriteThread.grpcTimestampToMicros;


public class SdkDriver {

    private final static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final static ObjectMapper jsonMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger logger = LoggerFactory.getLogger(SdkDriver.class);

    public static void main(String[] args)  throws Exception {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        if (args.length != 1) {
            logger.info("Must provide config.yaml");
            System.exit(-1);
        }
        run(args[0]);
    }

    static Op addOp(TestSuite.Run.Operation op, int count, TestSuite.Variables variables) {
        JsonObject initial = JsonObject.create().put(Strings.CONTENT_NAME, Strings.INITIAL_CONTENT_VALUE);
        JsonObject updated = JsonObject.create().put(Strings.CONTENT_NAME, Strings.UPDATED_CONTENT_VALUE);

        switch (op.op()) {
            case INSERT:
                return new OpInsert(initial, count, op.docLocation(), variables);
            case GET:
                return new OpGet(count, op.docLocation(), variables);
            case REMOVE:
                return new OpRemove(count, op.docLocation(), variables);
            case REPLACE:
                return new OpReplace(updated, count, op.docLocation(), variables);
            default:
                throw new IllegalArgumentException("Unknown op " + op);
        }
    }

    static BuiltSdkCommand createSdkCommand(TestSuite.Variables variables, TestSuite.Run run) {
        //TODO Make sure any REPLACE operations are in the YAML before REMOVES
        //StringBuilder sb = new StringBuilder();
        var ops = new ArrayList<Op>();

        run.operations().forEach(op -> {
            int count = evaluateCount(variables, op.count());
            ops.add(addOp(op, count, variables));
        });
        return new BuiltSdkCommand(ops, "Test");
    }

    private static int evaluateCount(TestSuite.Variables variables, String count){
        try {
            return Integer.parseInt(count);
        } catch (RuntimeException err) {
            if (count.startsWith("$")) {
                return variables.getCustomVarAsInt(count);
            }

            throw new IllegalArgumentException("Don't know how to handle repeated count " + count);
        }
    }

    static TestSuite readTestSuite(String configFilename) {
        try {
            return yamlMapper.readValue(new File(configFilename), TestSuite.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static JsonObject readTestSuiteAsJson(String configFilename) {
        try {
            var object = yamlMapper.readValue(new File(configFilename), Object.class);
            var bytes = jsonMapper.writeValueAsBytes(object);
            return JsonObject.fromJson(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Credit to https://stackoverflow.com/questions/52580008/how-does-java-application-know-it-is-running-within-a-docker-container
    public static Boolean isRunningInsideDocker() {
        try (Stream<String> stream =
                     Files.lines(Paths.get("/proc/1/cgroup"))) {
            return stream.anyMatch(line -> line.contains("/docker"));
        } catch (IOException e) {
            return false;
        }
    }

    static void run(String testSuiteFile) throws Exception {
        logger.info("Reading config file {}", testSuiteFile);
        var testSuite = readTestSuite(testSuiteFile);
        var testSuiteAsJson = readTestSuiteAsJson(testSuiteFile);

        var dbUrl = String.format("jdbc:postgresql://%s:%d/%s",
                isRunningInsideDocker() ? testSuite.connections().database().hostname_docker() : testSuite.connections().database().hostname(),
                testSuite.connections().database().port(),
                testSuite.connections().database().database());

        var props = new Properties();
        props.setProperty("user", testSuite.connections().database().username());
        props.setProperty("password", testSuite.connections().database().password());

        logger.info("Is running inside Docker {}", isRunningInsideDocker());
        logger.info("Connecting to database " + dbUrl);

        try (var conn = DriverManager.getConnection(dbUrl, props)) {

            var clusterHostname = isRunningInsideDocker() ? testSuite.connections().cluster().hostname_docker() : testSuite.connections().cluster().hostname();
            logger.info("Connecting to cluster {}", clusterHostname);

            var cluster = Cluster.connect(clusterHostname, testSuite.connections().cluster().username(), testSuite.connections().cluster().password());

            var storageBackend = StorageBackend.COUCHSTORE;
            var numReplaces = 0;

            var clusterJson = produceClusterJson(clusterHostname, testSuite.connections().cluster())
                    .put("storage", storageBackend.alias())
                    .put("replicas", numReplaces);

            // Use all the available memory for the bucket
            var memoryMb = clusterJson.getInt("memory");

            logger.info("(Re)creating bucket {} with memoryMB {} and backend {}", Defaults.DEFAULT_BUCKET, memoryMb, storageBackend);
            var bucketSettings = BucketSettings.create(Defaults.DEFAULT_BUCKET)
                    .ramQuotaMB(memoryMb)
                    .numReplicas(numReplaces)
                    .storageBackend(storageBackend);

            try {
                cluster.buckets().dropBucket(Defaults.DEFAULT_BUCKET);
            }
            catch (BucketNotFoundException ignored) {}

            try {
                cluster.buckets().createBucket(bucketSettings);
            }
            catch (BucketExistsException ignored) {}

            var createConnection =
                    ClusterConnectionCreateRequest.newBuilder()
                            .setClusterHostname(clusterHostname)
                            .setClusterUsername(testSuite.connections().cluster().username())
                            .setClusterPassword(testSuite.connections().cluster().password())
                            .setClusterConnectionId(UUID.randomUUID().toString())
                            .build();

            var performerHostname = isRunningInsideDocker() ? testSuite.connections().performer().hostname_docker() : testSuite.connections().performer().hostname();
            logger.info("Connecting to performer on {}:{}", performerHostname, testSuite.connections().performer().port());

            Performer performer = new Performer(
                    performerHostname,
                    testSuite.connections().performer().port(),
                    createConnection);

            for (TestSuite.Run run : testSuite.runs()) {
                logger.info("Running workload " + run);

                run.operations().forEach(op -> {
                    op.docLocation().poolSize(testSuite.variables()).ifPresent(docPoolSize -> {
                        var docThread = new DocCreateThread(docPoolSize,
                                cluster.bucket(Defaults.DEFAULT_BUCKET).defaultCollection());
                        docThread.start();
                        // wait for all docs to be created
                        logger.info("Waiting for document pool of size {} to be created", docPoolSize);
                        try {
                            docThread.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });



                BuiltSdkCommand command = createSdkCommand(testSuite.variables(), run);

                PerfRunHorizontalScaling.Builder horizontalScalingBuilt = PerfRunHorizontalScaling.newBuilder();

                command.sdkCommand().forEach(op -> {
                    op.applyTo(horizontalScalingBuilt);
                });

                PerfRunRequest.Builder perf = PerfRunRequest.newBuilder()
                        .setClusterConnectionId(createConnection.getClusterConnectionId());

                for (int i=0; i< testSuite.variables().horizontalScaling(); i++){
                    perf.addHorizontalScaling(horizontalScalingBuilt);
                }

                var done = new AtomicBoolean(false);
                DbWriteThread dbWrite = new DbWriteThread(conn, run.uuid(), done);
                dbWrite.start();
                var received = new AtomicInteger(0);
                var start = System.nanoTime();

                var responseObserver = new StreamObserver<PerfSingleResult>() {
                    @Override
                    public void onNext(PerfSingleResult perfRunResult) {
                        var got = received.incrementAndGet();
                        if (got % 1000 == 0 || got == 1) {
                            logger.info("Received {}", got);
                        }

                        if (perfRunResult.hasOperationResult()) {
                            dbWrite.addToQ(perfRunResult.getOperationResult());
                        }
                        else if (perfRunResult.hasMetricsResult()) {
                            // If this is a performance problem will need to have a queue.  It's assumed here that the
                            // performer is sending metrics back relatively infrequently, e.g. every second or more.
                            writeMetricResult(perfRunResult, conn, start, run);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        logger.error("Error from performer after receiving {}: {}", received.get(), throwable.toString());
                        done.set(true);
                        System.exit(-1);
                    }

                    @Override
                    public void onCompleted() {
                        logger.info("Performer has finished after receiving {}", received.get());
                        done.set(true);
                    }
                };

                performer.stubBlockFuture().perfRun(perf.build(), responseObserver);
                logger.info("Waiting for run to finish and data to be written to db");
                dbWrite.join();


                // Only insert into the runs table if everything was successful
                var jsonVars = JsonObject.create();
                testSuite.variables().custom().forEach(v -> jsonVars.put(v.name(), v.value()));
                testSuite.variables().predefined().forEach(v -> jsonVars.put(v.name().name().toLowerCase(), v.value()));

                // Bump this whenever anything changes on the driver side that means we can't compare results against previous ones.
                // (Will also need to force a rerun of tests for this language, since jenkins-sdk won't know it's occurred).
                jsonVars.put("driverVersion", 6);
                // todo jsonVars.put("performerVersion", performer.response().getPerformerVersion());

                var runJson = testSuiteAsJson.getArray("runs")
                        .toList().stream()
                        .map(v -> JsonObject.from((HashMap) v))
                        .filter(v -> v.getString("uuid").equals(run.uuid()))
                        .findFirst()
                        .get();
                runJson.removeKey("uuid");

                var json = JsonObject.create()
                        .put("cluster", clusterJson)
                        .put("impl", JsonObject.fromJson(jsonMapper.writeValueAsString(testSuite.impl())))
                        .put("workload", runJson)
                        .put("vars", jsonVars);
                logger.info(json.toString());

                try (var st = conn.createStatement()) {
                    String statement = String.format("INSERT INTO runs VALUES ('%s', NOW(), '%s') ON CONFLICT (id) DO UPDATE SET datetime = NOW(), params = '%s'",
                            run.uuid(),
                            json.toString(),
                            json.toString());
                    st.executeUpdate(statement);
                }


                logger.info("Finished!");
            }

        }
    }

    private static void writeMetricResult(PerfSingleResult perfRunResult, Connection conn, long start, TestSuite.Run run) {
        var metrics = perfRunResult.getMetricsResult();

        try (var st = conn.createStatement()) {
            var timeSinceStartSecs = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - start);

            var statement = String.format("INSERT INTO metrics VALUES (to_timestamp(%d), '%s', '%s', %d)",
                    TimeUnit.MICROSECONDS.toSeconds(grpcTimestampToMicros(metrics.getInitiated())),
                    run.uuid(),
                    metrics.getMetrics(),
                    timeSinceStartSecs);
            st.executeUpdate(statement);
            logger.info("Writing metrics {}", metrics.getMetrics());
        } catch (SQLException err) {
            logger.error("Failed to write metrics data to database", err);
        }
    }

    private static JsonObject produceClusterJson(String hostname, TestSuite.Connections.Cluster cluster) {
        var out = JsonObject.create();
        out.put("type", cluster.type());

        try {
            var httpClient = new OkHttpClient().newBuilder().build();

            var adminUsername = cluster.username();
            var adminPassword = cluster.password();

            var resp1 = httpClient.newCall(new Request.Builder()
                            .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                            .url("http://" + hostname + ":8091/pools/default")
                            .build())
                    .execute();

            var resp2 = httpClient.newCall(new Request.Builder()
                            .header("Authorization", Credentials.basic(adminUsername, adminPassword))
                            .url("http://" + hostname + ":8091/pools")
                            .build())
                    .execute();

            var raw1 = JsonObject.fromJson(resp1.body().bytes());
            var raw2 = JsonObject.fromJson(resp2.body().bytes());

            var node1 = ((JsonObject) raw1.getArray("nodes").get(0));

            out.put("nodeCount", raw1.getArray("nodes").size());
            out.put("memory", raw1.getInt("memoryQuota"));
            out.put("cpuCount", node1.getInt("cpuCount"));
            out.put("version", raw2.getString("implementationVersion"));
        } catch (IOException e) {
            logger.warn("Failed to get cluster info {}", e.getMessage());
        }

        return out;
    }

}
