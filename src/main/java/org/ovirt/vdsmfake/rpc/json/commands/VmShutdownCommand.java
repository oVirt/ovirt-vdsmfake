package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("VM.shutdown")
public class VmShutdownCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.shutdown(params.get("vmID").asText(),
                params.get("delay").asText(),
                params.get("message").asText());
    }

}
