package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings("rawtypes")
public class VmDestroyCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.destroy(params.get("vmID").asText());
    }

}
