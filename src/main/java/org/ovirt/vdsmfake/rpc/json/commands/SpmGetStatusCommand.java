package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings("rawtypes")
public class SpmGetStatusCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "spm_st";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.getSpmStatus(params
                .get("storagepoolID")
                .asText());
    }

}
