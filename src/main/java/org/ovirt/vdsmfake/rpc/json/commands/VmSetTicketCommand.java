package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

@SuppressWarnings("rawtypes")
public class VmSetTicketCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map activateApi(JsonNode params) throws JsonParseException, JsonMappingException, IOException {
        return api.setVmTicket(params.get("vmID").asText(),
                params.get("password").asText(),
                params.get("ttl").asText(),
                params.get("existingConnAction").asText(),
                toMap(params.get("params")));
    }

}
