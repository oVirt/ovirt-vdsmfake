package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings("rawtypes")
public class StoragePoolDisconnectCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.disconnectStoragePool(params.get("storagepoolID").asText(),
                params.get("hostID").asInt(),
                params.get("scsiKey").asText());
    }

}
