package org.ovirt.vdsmfake;

import org.ovirt.vdsmfake.rpc.json.JsonRpcServer;
import org.ovirt.vdsmfake.task.TaskProcessor;

public class FakeVDSM {
    public static void main(String args[]) throws Exception {
        AppConfig.getInstance().setNetworkBridgeName("ovirtmgmt");
        AppConfig.getInstance().setCacheDir("/var/log/vdsmfake/cache");
        AppConfig.getInstance().setConstantDelay(0);
        AppConfig.getInstance().setLogDir("/var/log/vdsmfake/xml");

        final TaskProcessor taskProcessor = TaskProcessor.getInstance();
        taskProcessor.init();

        JsonRpcServer server = new JsonRpcServer(54322);
        server.start();
        while (true) {
            Thread.sleep(1000);
        }
    }
}
