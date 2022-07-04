package com.sdk.config;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.grpc.sdk.protocol.CommandInsert;
import com.couchbase.grpc.sdk.protocol.Counter;
import com.couchbase.grpc.sdk.protocol.CounterGlobal;
import com.couchbase.grpc.sdk.protocol.PerfRunHorizontalScaling;
import com.couchbase.grpc.sdk.protocol.SdkCommand;
import com.couchbase.grpc.sdk.protocol.SdkWorkload;
import com.couchbase.grpc.sdk.protocol.Workload;

public record OpInsert(JsonObject content, int count, TestSuite.DocLocation location,
                       TestSuite.Variables variables) implements Op {
    @Override
    public void applyTo(PerfRunHorizontalScaling.Builder builder) {
        builder.addWorkloads(Workload.newBuilder()
                .setSdk(SdkWorkload.newBuilder()
                        .setCommand(SdkCommand.newBuilder()
                                .setInsert(CommandInsert.newBuilder()
                                        .setContentJson(content.toString())
                                        .setLocation(location.convert(variables))
                                        .build()))
                        .setCounter(Counter.newBuilder()
                                .setCounterId("counter1")
                                .setGlobal(CounterGlobal.newBuilder()
                                        .setCount(count)))));
    }
}