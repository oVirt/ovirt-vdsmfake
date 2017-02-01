package org.ovirt.vdsmfake.rpc.json;

import java.util.function.Consumer;

import org.ovirt.vdsm.jsonrpc.client.JsonRpcRequest;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;

public interface CommandExecutor {

    JsonRpcResponse process(JsonRpcRequest request);

    default Consumer<JsonRpcResponse> aroundConsume(Consumer<JsonRpcResponse> responseConsumer, String method) {
        return responseConsumer;
    }

    default void execute(JsonRpcRequest request, Consumer<JsonRpcResponse> responseConsumer) {
        JsonRpcResponse response = process(request);
        aroundConsume(responseConsumer, request.getMethod()).accept(response);
    }
}
