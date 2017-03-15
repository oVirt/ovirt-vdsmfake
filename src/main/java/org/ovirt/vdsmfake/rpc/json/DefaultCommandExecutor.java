package org.ovirt.vdsmfake.rpc.json;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Singleton;

import org.ovirt.vdsm.jsonrpc.client.JsonRpcRequest;
import org.ovirt.vdsm.jsonrpc.client.JsonRpcResponse;
import org.ovirt.vdsmfake.rpc.json.commands.JsonCommand;
import org.ovirt.vdsmfake.rpc.json.commands.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@DefaultExecutor
public class DefaultCommandExecutor implements CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(DefaultCommandExecutor.class);

    @Override
    public JsonRpcResponse process(JsonRpcRequest request) {
        JsonCommand jsonCommand = CDI.current().select(
                JsonCommand.class,
                new VerbLiteral(request.getMethod())).get();
        if (jsonCommand == null) {
            String msg = String.format("Couldn't instantiate a command instance from method %s", request.getMethod());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return jsonCommand.run(request.getParams(), request.getId());
    }

    class VerbLiteral extends AnnotationLiteral<Verb> implements Verb {

        private final String val;

        public VerbLiteral(String val) {
            this.val = val;
        }

        @Override
        public String value() {
            return val;
        }
    }
}
