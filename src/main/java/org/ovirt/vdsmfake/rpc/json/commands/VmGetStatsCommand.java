package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("VM.getStats")
public class VmGetStatsCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "statsList";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.getVmStats(params.get("vmID").asText());
    }

}
