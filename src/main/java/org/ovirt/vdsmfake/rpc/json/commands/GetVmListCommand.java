package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("Host.getVMList")
public class GetVmListCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "vmList";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map activateApi(JsonNode params) {
        return api.list();
    }

}
