package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("StoragePool.create")
public class StoragePoolCreateCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map activateApi(JsonNode params) throws IOException {
        return api.createStoragePool(0,
                params.get("storagepoolID").asText(),
                params.get("name").asText(),
                params.get("masterSdUUID").asText(),
                toList(params.get("domainList")),
                params.get("masterVersion").asInt(),
                null,
                params.get("lockRenewalIntervalSec").asInt(),
                params.get("leaseTimeSec").asInt(),
                params.get("ioOpTimeoutSec").asInt(),
                params.get("leaseRetries").asInt());
    }

}
