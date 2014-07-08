package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.ovirt.vdsmfake.domain.StorageDomain;

@SuppressWarnings("rawtypes")
public class StorageDomainCreateCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.createStorageDomain(StorageDomain.StorageType.NFS.ordinal(),
                params.get("storagedomainID").asText(),
                params.get("name").asText(),
                params.get("typeArgs").asText(),
                params.get("domainClass").asInt(),
                null);
    }

}
