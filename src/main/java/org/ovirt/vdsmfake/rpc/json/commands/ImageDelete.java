package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings({ "rawtypes" })
public class ImageDelete extends JsonCommand {

    @Override
    public String fieldName() {
        return "uuid";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.deleteImage(params.get("imageID").asText(),
                params.get("storagepoolID").asText(),
                params.get("storagedomainID").asText(),
                params.get("postZero").asBoolean(),
                params.get("force").asBoolean());
    }

}
