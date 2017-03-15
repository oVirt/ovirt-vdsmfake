package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("Host.getAllTasksStatuses")
public class GetAllTasksStatusesCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "allTasksStatus";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.getAllTasksStatuses();
    }

}
