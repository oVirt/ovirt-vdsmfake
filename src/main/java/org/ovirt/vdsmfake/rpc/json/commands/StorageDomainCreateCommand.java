package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.ovirt.vdsmfake.domain.StorageDomain;

@Verb("StorageDomain.create")
public class StorageDomainCreateCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @Override
    protected Map activateApi(JsonNode params) throws IOException {
        return api.createStorageDomain(StorageDomain.StorageType.NFS.ordinal(),
                params.get("storagedomainID").asText(),
                params.get("name").asText(),
                params.get("typeArgs").asText(),
                params.get("domainClass").asInt(),
                null);
    }

}
