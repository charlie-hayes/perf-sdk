package com.couchbase.sdk.perf;

import com.couchbase.grpc.sdk.protocol.PerfRunHorizontalScaling;
import com.couchbase.grpc.sdk.protocol.PerfRunRequest;
import com.couchbase.grpc.sdk.protocol.PerfSingleOperationResult;
import com.couchbase.grpc.sdk.protocol.PerfSingleResult;
import com.couchbase.sdk.SdkOperation;
import com.couchbase.sdk.utils.ClusterConnection;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PerfMarshaller creates however many threads need to run in tandem based on the given horizontal scaling value
 */
public class PerfMarshaller {
    private static final Logger logger = LoggerFactory.getLogger(PerfMarshaller.class);
    private static ConcurrentLinkedQueue<PerfSingleOperationResult> writeQueue = new ConcurrentLinkedQueue<>();
    private static AtomicBoolean done = new AtomicBoolean();

    public static void run(ClusterConnection connection,
                           PerfRunRequest perfRun,
                           StreamObserver<PerfSingleResult> responseObserver) throws InterruptedException {
        try{
            PerfWriteThread writer = new PerfWriteThread(
                    responseObserver,
                    writeQueue,
                    done);

            // There is a counter for each operation as when a single one was used for all operations,
            // one thread would finish a part of a workload and set the counter to 0 before the other workload was done

            // todo can probably remove
            var removeCounter = new AtomicInteger();
            var replaceCounter = new AtomicInteger();
            var getCounter = new AtomicInteger();

            List<PerfRunnerThread> runners = new ArrayList<>();
            for (int runnerIndex = 0; runnerIndex < perfRun.getHorizontalScalingCount(); runnerIndex ++) {
                PerfRunHorizontalScaling perThread = perfRun.getHorizontalScaling(runnerIndex);
                runners.add(new PerfRunnerThread(runnerIndex,
                        connection,
                        perThread,
                        removeCounter,
                        replaceCounter,
                        getCounter,
                        writer));
            }

            logger.info("Starting writer thread");
            writer.start();

            for (PerfRunnerThread perfRunnerThread : runners) {
                perfRunnerThread.start();
            }
            logger.info("Started {} threads", runners.size());

            for (PerfRunnerThread runner : runners) {
                runner.join();
            }
            logger.info("All {} threads completed", runners.size());
            done.set(true);

            writer.join();
            logger.info("Writer thread completed");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}

class PerfRunnerThread extends Thread {
    private final Logger logger;
    private final ClusterConnection connection;
    private final PerfRunHorizontalScaling perThread;
    private final AtomicInteger removeCounter;
    private final AtomicInteger replaceCounter;
    private final AtomicInteger getCounter;
    private final PerfWriteThread writeQueue;

    PerfRunnerThread(int runnerIndex,
                     ClusterConnection connection,
                     PerfRunHorizontalScaling perThread,
                     AtomicInteger removeCounter,
                     AtomicInteger replaceCounter,
                     AtomicInteger getCounter,
                     PerfWriteThread writer) {
        logger = LoggerFactory.getLogger("runner-" + runnerIndex);
        this.connection = connection;
        this.perThread = perThread;
        this.removeCounter = removeCounter;
        this.replaceCounter = replaceCounter;
        this.getCounter = getCounter;
        this.writeQueue = writer;
    }

    @Override
    public void run() {
        var operation = new SdkOperation(removeCounter, replaceCounter, getCounter);
        int operationsSuccessful = 0;
        int operationsFailed = 0;

        for (var command : perThread.getSdkCommandList()){
            if (command.hasSdk()) {
                var sdkWorkload = command.getSdk();

                for (int i = 0; i < sdkWorkload.getCount(); i++) {
                    var result = operation.run(connection, sdkWorkload);
                    writeQueue.enqueue(result);
                    if (result.getSdkResult().getSuccess()) {
                        operationsSuccessful += 1;
                    }
                    else {
                        operationsFailed += 1;
                    }
                }
            }
        }

        logger.info("Finished after {} successful operations and {} failed",
                operationsSuccessful, operationsFailed);
    }
}