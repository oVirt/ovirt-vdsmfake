package org.ovirt.vdsmfake.rpc.json;


import javax.inject.Singleton;

import org.ovirt.vdsm.jsonrpc.client.JsonRpcRequest;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;

@Singleton
@DefaultExecutor
public class DefaultCommandExecutor implements CommandExecutor {

    @Override
    public JsonRpcResponse process(JsonRpcRequest request) {
        return CommandFactory.createCommand(request.getMethod())
                .run(request.getParams(), request.getId()); }
}
