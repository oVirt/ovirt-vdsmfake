package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@SuppressWarnings("rawtypes")
public class VmCreateCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "vmList";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        Map apiData = toMap(params.get("vmParams"));
        if (apiData.get("devices") != null) {
            apiData.put("devices", ((List<Object>) apiData.get("devices")).toArray());
        }
        return api.create(apiData);
    }

}
