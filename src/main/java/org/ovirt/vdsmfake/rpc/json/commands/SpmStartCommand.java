package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings("rawtypes")
public class SpmStartCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "uuid";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.spmStart(params.get("storagepoolID").asText(),
                params.get("prevID").asInt(),
                params.get("prevLver").asText(),
                0,
                params.get("enableScsiFencing").asText(),
                params.get("maxHostID").asInt(),
                null);
    }

}
