package org.ovirt.vdsmfake.rpc.json;

import org.ovirt.vdsmfake.rpc.json.commands.GetAllTasksInfoCommand;
import org.ovirt.vdsmfake.rpc.json.commands.GetAllTasksStatusesCommand;
import org.ovirt.vdsmfake.rpc.json.commands.GetAllVmStatsCommand;
import org.ovirt.vdsmfake.rpc.json.commands.GetCapabilitiesCommand;
import org.ovirt.vdsmfake.rpc.json.commands.GetFullVmListCommand;
import org.ovirt.vdsmfake.rpc.json.commands.GetHardwareInfoCommmand;
import org.ovirt.vdsmfake.rpc.json.commands.GetStatsCommand;
import org.ovirt.vdsmfake.rpc.json.commands.GetVmListCommand;
import org.ovirt.vdsmfake.rpc.json.commands.HostDevListByCaps;
import org.ovirt.vdsmfake.rpc.json.commands.HostGetStorageDomainsCommand;
import org.ovirt.vdsmfake.rpc.json.commands.ImageDelete;
import org.ovirt.vdsmfake.rpc.json.commands.JsonCommand;
import org.ovirt.vdsmfake.rpc.json.commands.SpmGetStatusCommand;
import org.ovirt.vdsmfake.rpc.json.commands.SpmStartCommand;
import org.ovirt.vdsmfake.rpc.json.commands.SpmStopCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StorageDomainActivateCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StorageDomainCreateCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StorageDomainGetInfoCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StorageDomainGetStatsCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StoragePoolConnectCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StoragePoolCreateCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StoragePoolDisconnectCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StoragePoolGetInfo;
import org.ovirt.vdsmfake.rpc.json.commands.StoragePoolGetIsoListCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StoragePoolRefreshCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StorageServerConnectCommand;
import org.ovirt.vdsmfake.rpc.json.commands.StorageServerDisconnectCommand;
import org.ovirt.vdsmfake.rpc.json.commands.TaskClearCommand;
import org.ovirt.vdsmfake.rpc.json.commands.TaskGetStatusCommand;
import org.ovirt.vdsmfake.rpc.json.commands.TaskRevertCommand;
import org.ovirt.vdsmfake.rpc.json.commands.TaskStopCommand;
import org.ovirt.vdsmfake.rpc.json.commands.UnsupportedCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VmCreateCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VmDestroyCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VmGetStatsCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VmMigrateCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VmSetTicketCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VmShutdownCommand;
import org.ovirt.vdsmfake.rpc.json.commands.VolumeCreateCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandFactory {
    private static final Logger log = LoggerFactory
            .getLogger(CommandFactory.class);

    public static JsonCommand createCommand(String methodName) {

        switch (methodName) {
        case "Host.getCapabilities":
            return new GetCapabilitiesCommand();
        case "Host.getVMList":
            return new GetVmListCommand();
        case "Host.getStats":
            return new GetStatsCommand();
        case "Host.getAllVmStats":
            return new GetAllVmStatsCommand();
        case "Host.getHardwareInfo":
            return new GetHardwareInfoCommmand();
        case "Host.getAllTasksStatuses":
            return new GetAllTasksStatusesCommand();
        case "Host.getAllTasksInfo":
            return new GetAllTasksInfoCommand();
        case "Host.getStorageDomains":
            return new HostGetStorageDomainsCommand();
        case "Host.getVMFullList":
            return new GetFullVmListCommand();
        case "Host.HostDevListByCaps":
            return new HostDevListByCaps();
        case "VM.create":
            return new VmCreateCommand();
        case "VM.destroy":
            return new VmDestroyCommand();
        case "VM.shutdown":
            return new VmShutdownCommand();
        case "VM.getStats":
            return new VmGetStatsCommand();
        case "VM.migrate":
            return new VmMigrateCommand();
        case "VM.setTicket":
            return new VmSetTicketCommand();
        case "StoragePool.spmStart":
            return new SpmStartCommand();
        case "StoragePool.spmStop":
            return new SpmStopCommand();
        case "StoragePool.getSpmStatus":
            return new SpmGetStatusCommand();
        case "StoragePool.connect":
            return new StoragePoolConnectCommand();
        case "StoragePool.disconnect":
            return new StoragePoolDisconnectCommand();
        case "StoragePool.connectStorageServer":
            return new StorageServerConnectCommand();
        case "StoragePool.disconnectStorageServer":
            return new StorageServerDisconnectCommand();
        case "StoragePool.getInfo":
            return new StoragePoolGetInfo();
        case "StoragePool.refresh":
            return new StoragePoolRefreshCommand();
        case "StoragePool.getIsoList":
            return new StoragePoolGetIsoListCommand();
        case "StoragePool.create":
            return new StoragePoolCreateCommand();
        case "StorageDomain.activate":
            return new StorageDomainActivateCommand();
        case "StorageDomain.getInfo":
            return new StorageDomainGetInfoCommand();
        case "StorageDomain.getStats":
            return new StorageDomainGetStatsCommand();
        case "StorageDomain.create":
            return new StorageDomainCreateCommand();
        case "Volume.create":
            return new VolumeCreateCommand();
        case "Task.getStatus":
            return new TaskGetStatusCommand();
        case "Task.stop":
            return new TaskStopCommand();
        case "Task.clear":
            return new TaskClearCommand();
        case "Task.revert":
            return new TaskRevertCommand();
        case "Image.delete":
            return new ImageDelete();
        default:
            // TODO: Support Host.setMOMPolicyParameters
            Exception e = new Exception();
            log.error("Unsupported method " + methodName, e);
            return new UnsupportedCommand();

        }
    }
}
