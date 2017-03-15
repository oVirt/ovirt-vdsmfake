package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@Verb("StoragePool.disconnectStorageServer")
public class StorageServerDisconnectCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "statuslist";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.disconnectStorageServer(params.get("domainType").asInt(),
                params.get("storagepoolID").asText(),
                toList(params.get("connectionParams")));
    }

}
