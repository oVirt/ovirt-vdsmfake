package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("Volume.create")
public class VolumeCreateCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "uuid";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.createVolume(params.get("storagedomainID").asText(),
                params.get("storagepoolID").asText(),
                params.get("imageID").asText(),
                params.get("size").asInt(),
                params.get("volFormat").asInt(),
                params.get("preallocate").asInt(),
                params.get("diskType").asInt(),
                params.get("srcVolUUID").asText(),
                params.get("desc").asText(),
                params.get("srcImgUUID").asText(),
                params.get("volumeID").asText());
    }

}
