package org.ovirt.vdsmfake.rpc.json.commands;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

@Verb("Host.dumpxmls")
public class DumpxmlsCommand extends JsonCommand {

    @Override
    public String fieldName() {
        return "vmList";
    }

    @Override
    protected Map activateApi(JsonNode params) throws IOException {
        return api.getAllTasksInfo();
    }

}
