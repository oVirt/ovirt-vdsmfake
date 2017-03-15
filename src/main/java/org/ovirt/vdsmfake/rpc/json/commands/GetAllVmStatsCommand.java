package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("Host.getAllVmStats")
public class GetAllVmStatsCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "statsList";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.getAllVmStats();
    }

}
