package org.ovirt.vdsmfake.rpc.json;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.ovirt.vdsm.jsonrpc.client.ClientConnectionException;
import org.ovirt.vdsm.jsonrpc.client.reactors.ReactorClient;
import org.ovirt.vdsm.jsonrpc.client.reactors.stomp.StompCommonClient;
import org.ovirt.vdsm.jsonrpc.client.reactors.stomp.impl.Message;
import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.domain.VM;
import org.ovirt.vdsmfake.domain.VdsmManager;
import org.ovirt.vdsmfake.task.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonRpcNotification {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcNotification.class);
    private static final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(AppConfig.getInstance().getEventsThreadPoolSize());

    private String messageFormatter(String msg, String vmid) {
        ObjectNode vmDetailNode = new ObjectMapper().createObjectNode();
        vmDetailNode.put("status", msg);
        vmDetailNode.put("hash", Integer.toString(vmid.hashCode()));

        ObjectNode paramsNode = new ObjectMapper().createObjectNode();
        paramsNode.put(vmid.toString(), vmDetailNode);
        paramsNode.put("notify_time", System.nanoTime());

        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("params", paramsNode);
        node.put("jsonrpc","2.0");
        node.put("method", "|virt|VM_status|" + vmid);
        return node.toString();
    }

    private void sendNotification(String message, String vmId, boolean removeClient) throws ClientConnectionException {
        if (message == null){
            log.warn("empty message has arrived, ignore empty messages");
        }
        send(messageFormatter(message, vmId), vmId, removeClient);
    }

    private void send(String message, String vmID, boolean removeClient) throws ClientConnectionException{
        ReactorClient client = null;
        try {
            // get client
            client = JsonRpcServer.getClientByVmId(vmID);

            // send message
            ((StompCommonClient) client).send((new Message()).message()
                    .withHeader("destination", "jms.queue.events")
                    .withContent(message.getBytes())
                    .build());
            log.debug("sending events message {}", message);
        } catch (Exception e) {
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
                vmUpdateStatus(vm, VM.VMStatus.WaitForLaunch, delay, WaitForLaunch, false);
                break;

            case START_VM_POWERING_UP:
                vmUpdateStatus(vm, VM.VMStatus.PoweringUp, delay, PoweringUp, false);
                break;

            case START_VM_AS_UP:
                vmUpdateStatus(vm, VM.VMStatus.Up, delay, Up, true); // last event for start vm flow
                break;

            case SHUTDOWN_VM:
                vmUpdateStatus(vm, VM.VMStatus.PoweredDown, delay, PoweredDown, false);
                // remove vm from vdsm.
                if (vm != null) {
                    vm.getHost().getRunningVMs().remove(vm.getId());
                }
                vmUpdateStatus(vm, VM.VMStatus.Down, 0, Down, true); // last event for stop vm flow
                break;

            default:
                log.error("Unhandled status detected.");
                break;
        }
    }

    // TODO: enlarge this method to support cross entities objects such as storage, hosts (currently BaseObject not
    // implement status).
    private void vmUpdateStatus(final VM vm, final VM.VMStatus status, final long delay, final String msg,
            final boolean removeClient)
            throws InterruptedException {
        scheduledExecutorService.schedule(() -> {
            try {
                vm.setStatus(status);
                sendNotification(msg, vm.getId(), removeClient);
                log.info("VM {} set to {}", vm.getId(), msg);

                // update host if required
                if (isUpdateRequired(status)) {
                    VdsmManager.getInstance().updateHost(vm.getHost());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        },
                delay,
                TimeUnit.MILLISECONDS);
    }

    private boolean isUpdateRequired(VM.VMStatus status){
        //TODO: fill this list on going.
        // list of statuses which required vdsm update {up, prowerdown, paused}
        return status == VM.VMStatus.Up ? true : status == VM.VMStatus.PoweredDown ? true : status == VM.VMStatus.Paused ? true : false;
    }
}
