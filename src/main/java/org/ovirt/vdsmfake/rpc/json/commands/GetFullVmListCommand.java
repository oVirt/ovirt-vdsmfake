package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetFullVmListCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "vmList";
    }

    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        Map data;

        if (params != null && params.get("vmList") != null) {
            data = api.list(true, toList(params.get("vmList")));
        } else {
            data = api.list(true, new ArrayList());
        }
        data.put("vmList", ((List) data.get("vmList")).toArray());
        return data;
    }

}
