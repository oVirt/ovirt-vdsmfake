package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@SuppressWarnings("rawtypes")
public class StoragePoolGetInfo extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.getStoragePoolInfo(params.get("storagepoolID").asText());
    }

}
