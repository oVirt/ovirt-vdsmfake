package org.ovirt.vdsmfake.rpc.json.commands;

import org.codehaus.jackson.JsonNode;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class HostDevListByCaps extends JsonCommand {

    @Override
    public String fieldName() {
        return "info";
    }

    @Override
    protected Map activateApi(JsonNode params) {
        return api.HostDevListByCaps();
    }

}
