package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;
import org.ovirt.vdsm.jsonrpc.client.RequestBuilder;
import org.ovirt.vdsm.jsonrpc.client.ResponseBuilder;
import org.ovirt.vdsmfake.service.ResultCodes;

@SuppressWarnings("rawtypes")
public class UnsupportedCommand extends JsonCommand {

    @Override
    public JsonRpcResponse run(JsonNode params, JsonNode requestId) {
        return new ResponseBuilder(requestId).withError(ResultCodes.UNSUPPORTED.map()).build();
    }

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return null;
    }

}
