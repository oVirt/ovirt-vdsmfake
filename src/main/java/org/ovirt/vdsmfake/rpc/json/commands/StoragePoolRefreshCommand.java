package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@Verb("StoragePool.refresh")
public class StoragePoolRefreshCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.refreshStoragePool(params.get("storagepoolID").asText(),
                params.get("masterSdUUID").asText(),
                params.get("masterVersion").asInt());
    }

}
