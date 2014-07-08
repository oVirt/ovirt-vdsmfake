package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@SuppressWarnings("rawtypes")
public class StorageDomainGetInfoCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "info";
    }

    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.getStorageDomainInfo(params.get("storagedomainID").asText());
    }

}
