package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@SuppressWarnings("rawtypes")
@Verb("Host.getStorageDomains")
public class HostGetStorageDomainsCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "domlist";
    }

    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.getStorageDomainsList(params.get("storagepoolID").asText(),
                params.get("domainClass").asInt(),
                params.get("storageType").asInt(),
                params.get("remotePath").asText());
    }

}
