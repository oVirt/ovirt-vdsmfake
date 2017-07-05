package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("StoragePool.connectStorageServer")
public class StorageServerConnectCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "statuslist";
    }

    @Override
    protected Map activateApi(JsonNode params) throws IOException {


        return api.connectStorageServer(params.get("domainType").asInt(),
                params.get("storagepoolID").asText(),
                toList(params.get("connectionParams")));
    }

}
