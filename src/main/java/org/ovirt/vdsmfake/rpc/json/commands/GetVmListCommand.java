package org.ovirt.vdsmfake.rpc.json.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

@SuppressWarnings({ "rawtypes" })
public class GetVmListCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "vmList";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map activateApi(JsonNode params) {
        Map data = api.list();
        List<String> justVmIdList = new ArrayList();
        for (Map<String, String> vmItem : (List<Map>) data.get("vmList")) {
            justVmIdList.add(vmItem.get("vmId"));
        }
        data.put("vmList", justVmIdList);
        return data;
    }

}
