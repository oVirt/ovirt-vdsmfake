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

        boolean encrypted = false;

        String encryptedProp = System.getProperty("fake.encrypted");
        if (encryptedProp != null) {
            encrypted = true;
        }

        JsonRpcServer server = new JsonRpcServer("localhost", 54322, encrypted);
        server.start();
        while (true) {
            Thread.sleep(1000);
        }
    }
}
