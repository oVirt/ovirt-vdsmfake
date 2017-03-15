package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("StoragePool.connect")
public class StoragePoolConnectCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.connectStoragePool(params.get("storagepoolID").asText(),
                params.get("hostID").asInt(),
                params.get("scsiKey").asText(),
                params.get("masterSdUUID").asText(),
                params.get("masterVersion").asInt());
    }

}
