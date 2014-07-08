package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.ovirt.vdsm.jsonrpc.client.ResponseBuilder;
import org.ovirt.vdsmfake.service.AbstractService;

@SuppressWarnings("rawtypes")
public class UnsupportedCommand extends JsonCommand {

    @SuppressWarnings("unchecked")
    @Override
    public ResponseBuilder run(JsonNode params, ResponseBuilder builder) {
        return builder.withResult(AbstractService.getStatusMap("OK", 0));
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
