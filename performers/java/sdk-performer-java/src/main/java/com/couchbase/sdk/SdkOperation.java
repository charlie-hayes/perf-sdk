package com.couchbase.sdk;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.grpc.sdk.protocol.*;
import com.couchbase.sdk.utils.ClusterConnection;
import io.grpc.stub.StreamObserver;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SdkOperation {
    private String name;
    private Map<Integer, String> docIds;

    public com.couchbase.grpc.sdk.protocol.SdkCommandResult run(
            ClusterConnection connection,
            SdkCreateRequest req
    ) {
        this.name = req.getName();

        AtomicInteger attemptCount = new AtomicInteger(-1);
        final int count = attemptCount.incrementAndGet();
        int attemptToUse = Math.min(count, req.getAttemptsCount() - 1);
        SdkAttemptRequest attempt = req.getAttempts(attemptToUse);

        for (SdkCommand command : attempt.getCommandsList()) {
            performOperation(connection, command);
        }
        SdkCommandResult.Builder response = SdkCommandResult.getDefaultInstance().newBuilderForType();
        return response.build();
    }

    private String convertDocId(DocId docId) {
        if (docId.getDocId().startsWith("__doc_")) {
            Integer docOrdinal = Integer.parseInt(docId.getDocId().split("__doc_")[1]);
            if (docIds == null) {
                docIds = new HashMap<>();
            }
            String cached = docIds.get(docOrdinal);
            if (cached == null) {
                cached = UUID.randomUUID().toString();
                docIds.put(docOrdinal, cached);
            }
            return cached;
        }
        else {
            return docId.getDocId();
        }
    }

    private void performOperation(ClusterConnection connection,
                                  SdkCommand op) {
        if (op.hasInsert()){
            final CommandInsert request = op.getInsert();
            final Collection collection = connection.getBucket().scope(request.getDocId().getScopeName()).collection(request.getDocId().getCollectionName());
            JsonObject content = JsonObject.fromJson(request.getContentJson());
            collection.insert(convertDocId(request.getDocId()), content);
        }else {
        throw new InternalPerformerFailure(new IllegalArgumentException("Unknown operation"));
    }
    }
}
