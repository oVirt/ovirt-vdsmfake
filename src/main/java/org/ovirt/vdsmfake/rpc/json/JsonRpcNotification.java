package org.ovirt.vdsmfake.rpc.json;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.ovirt.vdsm.jsonrpc.client.ClientConnectionException;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorClient;
import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.domain.VM;
import org.ovirt.vdsmfake.domain.VdsmManager;
import org.ovirt.vdsmfake.task.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public class JsonRpcNotification {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcNotification.class);
    private static final ExecutorService service = Executors.newFixedThreadPool(AppConfig.getInstance()
            .getEventsThreadPoolSize(), new BasicThreadFactory.Builder()
            .namingPattern("events-service-pool-%d")
            .daemon(true)
            .priority(Thread.MAX_PRIORITY)
            .build());


    private String messageFormatter(String msg, String vmid) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("params", String.valueOf(System.nanoTime()));
        objectNode.put("status", msg);
        objectNode.put("hash", Integer.toString(vmid.hashCode()));
        objectNode.put("jsonrpc","2.0");
        objectNode.put("method", "|virt|VM_status|" + vmid);
        return objectNode.toString();
    }

    private void sendNotification(String message, String vmId, boolean removeClient) throws ClientConnectionException {
        if (message == null){
            log.warn("empty message has arrived, ignore empty messages");
        }

        //send
        send(messageFormatter(message, vmId), vmId, removeClient);
    }

    private void send(String message, String vmID, boolean removeClient) throws ClientConnectionException{
        ReactorClient client = null;
        try {
            //get client
            client = JsonRpcServer.getClientByVmId(vmID);

            //send message
            client.sendMessage(message.getBytes());
            log.debug("sending events message {}", message);

        }catch (ClientConnectionException e) {
            log.error("Host {}, failed to send event message {}", client.getHostname(), e);
        }

        // remove client from the map in case the operation completed \ last event was sent.
        if (removeClient){
            JsonRpcServer.removeClientByVmId(vmID);
        }
    }

    public void fireEvents(TaskType taskType, long delay, Object entity) throws InterruptedException {

        final String WaitForLaunch = "WaitForLaunch";
        final String PoweringUp = "Powering up";
        final String Up = "Up";
        final String PoweredDown = "Powering down";
        final String Down = "Down";

        VM vm = (VM) entity;

        //TODO: merged duplicate code 'TaskRequest.process()'
        switch (taskType){
            case START_VM:
                vmUpdateStatus(vm, VM.VMStatus.WaitForLaunch, delay, WaitForLaunch, service, false);
                vmUpdateStatus(vm, VM.VMStatus.PoweringUp, delay, PoweringUp, service, false);
                break;

            case START_VM_AS_UP:
                vmUpdateStatus(vm, VM.VMStatus.Up, delay, Up, service, true); // last event for start vm flow
                break;

            case SHUTDOWN_VM:
                vmUpdateStatus(vm, VM.VMStatus.PoweredDown, delay, PoweredDown, service, false);
                // remove vm from vdsm.
                if (vm != null) {
                    vm.getHost().getRunningVMs().remove(vm.getId());
                }
                vmUpdateStatus(vm, VM.VMStatus.Down, 0, Down, service, true); // last event for stop vm flow
                break;

            default:
                log.error("Unhandled status detected.");
                break;
        }
    }

    private class EventHandler extends Thread {
        private VM vm;
        private VM.VMStatus status;
        private long delay;
        private String msg;
        private boolean removeClient;

        private EventHandler(final VM vm, final VM.VMStatus status, final long delay, final String msg,
                             final boolean removeClient){
            this.vm = vm;
            this.status = status;
            this.delay = delay;
            this.msg = msg;
            this.removeClient = removeClient;
        }

        public void run(){
            try {
                TimeUnit.MILLISECONDS.sleep(delay); // simulate real life delays.
                vm.setStatus(status);
                sendNotification(msg, vm.getId(), removeClient);
                log.info("VM {} set to {}", vm.getId(), msg);

                // update host if required
                if (isUpdateRequired(status)){
                    VdsmManager.getInstance().updateHost(vm.getHost());
                }
            }catch (Exception e){
                log.error(e.toString());
            }
        }
    }

    //TODO: enlarge this method to support cross entities objects such as storage, hosts (currently BaseObject not implement status).
    private void vmUpdateStatus(final VM vm, final VM.VMStatus status, final long delay, final String msg,
                                final ExecutorService service,
                                final boolean removeClient)
            throws InterruptedException {
        service.submit(new EventHandler(vm, status, delay, msg, removeClient));
    }

    private boolean isUpdateRequired(VM.VMStatus status){
        //TODO: fill this list on going.
        // list of statuses which required vdsm update {up, prowerdown, paused}
        return status == VM.VMStatus.Up ? true : status == VM.VMStatus.PoweredDown ? true : status == VM.VMStatus.Paused ? true : false;
    }
}
