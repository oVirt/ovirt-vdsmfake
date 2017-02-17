package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings({ "rawtypes" })
public class VolumeGetInfoCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "uuid";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.getVolumeInfo(params.get("storagedomainID").asText(), params.get("storagepoolID").asText(),
                params.get("imageID").asText(), params.get("volumeID").asText());
    }
}
