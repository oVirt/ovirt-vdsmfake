/**
 Copyright (c) 2012 Red Hat, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

           http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package org.ovirt.vdsmfake.rpc;

import java.util.List;
import java.util.Map;

import org.ovirt.vdsmfake.service.AbstractService;
import org.ovirt.vdsmfake.service.HostService;
import org.ovirt.vdsmfake.service.NetworkService;
import org.ovirt.vdsmfake.service.StorageService;
import org.ovirt.vdsmfake.service.TaskService;
import org.ovirt.vdsmfake.service.VMService;

/**
 *
 *
 */
@SuppressWarnings({ "rawtypes" })
public class Api extends AbstractService {

    final StorageService storageService = StorageService.getInstance();
    final VMService vmService = VMService.getInstance();
    final NetworkService networkService = NetworkService.getInstance();
    final HostService hostService = HostService.getInstance();
    final TaskService taskService = TaskService.getInstance();

    public Api() {
    }

    public Map create(Map createInfo) {
        return vmService.create(createInfo);
    }

    public Map destroy(String vmId) {
        return vmService.destroy(vmId);
    }

    public Map shutdown(String vmId, String timeout, String message) {
        return vmService.shutdown(vmId, timeout, message);
    }

    public Map shutdownHost(int reboot) {
        return getOKStatusNotImplemented();
    }

    public Map pause(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map hibernate(String vmId, String hiberVolHandle) {
        return getOKStatusNotImplemented();
    }

    public Map shutdown(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map reset(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map cont(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map list() {
        return vmService.list();
    }

    public Map list(boolean isFull, List<String> vmIds) {
        return vmService.list(isFull, vmIds);
    }

    public Map getVdsCapabilities() {
        return hostService.getVdsCapabilities();
    }

    public Map getVdsHardwareInfo() {
        return hostService.getVdsHardwareInfo();
    }

    public Map getVdsStats() {
        return hostService.getVdsStats();
    }

    public Map desktopLogin(String vmId, String domain, String user, String password) {
        return getOKStatusNotImplemented();
    }

    public Map desktopLogoff(String vmId, String force) {
        return getOKStatusNotImplemented();
    }

    public Map desktopLock(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map getVmStats(String vmId) {
        return vmService.getVmStats(vmId);
    }

    public Map getAllVmStats() {
        return vmService.getAllVmStats();
    }

    public Map getAllVmRuntimeStats() {
        return vmService.getAllVmRuntimeStats();
    }

    public Map getAllVmDeviceStats() {
        return vmService.getAllVmDeviceStats();
    }

    public Map getVmStatus(List<String> vmIds) {
        return vmService.getVmStatus(vmIds);
    }

    public Map getVmConfInfo(List<String> vmIds) {
        return vmService.getVmConfInfo(vmIds);
    }

    public Map getVmGuestDetails(List<String> vmIds) {
        return vmService.getVmGuestDetails(vmIds);
    }

    public Map migrate(Map<String, String> migrationInfo) {
        return vmService.migrate(migrationInfo);
    }

    public Map migrateStatus(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map migrateCancel(String vmId) {
        return getOKStatusNotImplemented();
    }

    public Map changeCD(String vmId, String imageLocation) {
        return getOKStatusNotImplemented();
    }

    public Map changeFloppy(String vmId, String imageLocation) {
        return getOKStatusNotImplemented();
    }

    public Map heartBeat() {
        return getOKStatusNotImplemented();
    }

    public Map monitorCommand(String vmId, String monitorCommand) {
        return getOKStatusNotImplemented();
    }

    public Map sendHcCmdToDesktop(String vmId, String hcCommand) {
        return getOKStatusNotImplemented();
    }

    public Map setVmTicket(String vmId, String otp64, String sec) {
        return getOKStatusNotImplemented();
    }

    public Map setVmTicket(String vmId, String otp64, String sec, String connectionAction, Map<String, String> params) {
        return vmService.setVmTicket(vmId, otp64, sec, connectionAction, params);
    }

    public Map startSpice(String vdsIp, int port, String ticket) {
        return getOKStatusNotImplemented();
    }

    public Map addNetwork(String bridge, String vlan, String bond, List<String> nics,
            Map<String, String> options) {
        return getOKStatusNotImplemented();
    }

    public Map delNetwork(String bridge, String vlan, String bond, List<String> nics) {
        return getOKStatusNotImplemented();
    }

    public Map editNetwork(String oldBridge, String newBridge, String vlan, String bond, List<String> nics,
            Map<String, String> options) {
        return getOKStatusNotImplemented();
    }

    public Map setupNetworks(Map networks, Map bonding, Map options) {
        return getOKStatusNotImplemented();
    }

    public Map setSafeNetworkConfig() {
        return getOKStatusNotImplemented();
    }

    public Map fenceNode(String ip, String port, String type, String user, String password,
            String action, String secured, String options) {
        return getOKStatusNotImplemented();
    }

    public Map connectStorageServer(int serverType, String spUUID, List<Map> args) {
        return storageService.connectStorageServer(serverType, spUUID, args);
    }

    public Map validateStorageServerConnection(int serverType, String spUUID, List<Map> args) {
        return storageService.validateStorageServerConnection(serverType, spUUID, args);
    }

    public Map disconnectStorageServer(int serverType, String spUUID, List<Map> args) {
        return storageService.disconnectStorageServer(serverType, spUUID, args);
    }

    public Map getStorageConnectionsList(String spUUID) {
        return getOKStatusNotImplemented();
    }

    public Map validateStorageDomain(String sdUUID) {
        return getOKStatusNotImplemented();
    }

    public Map createStorageDomain(int domainType, String sdUUID, String domainName, String arg,
            int storageType, String storageFormatType)
    {
        return storageService.createStorageDomain(domainType, sdUUID, domainName, arg, storageType, storageFormatType);
    }

    public Map formatStorageDomain(String sdUUID) {
        return getOKStatusNotImplemented();
    }

    public Map connectStoragePool(String spUUID, int hostSpmId, String SCSIKey, String masterdomainId,
            int masterVersion) {
        return storageService.connectStoragePool(spUUID, hostSpmId, SCSIKey, masterdomainId, masterVersion);
    }

    public Map disconnectStoragePool(String spUUID, int hostSpmId, String SCSIKey) {
        return getOKStatusNotImplemented();
    }

    public Map createStoragePool(int poolType, String spUUID, String poolName, String msdUUID,
            List<String> domList, int masterVersion, String lockPolicy, int lockRenewalIntervalSec, int leaseTimeSec,
            int ioOpTimeoutSec, int leaseRetries) {
        return storageService.createStoragePool(
                poolType,
                spUUID,
                poolName,
                msdUUID,
                domList,
                masterVersion,
                lockPolicy,
                lockRenewalIntervalSec,
                leaseTimeSec,
                ioOpTimeoutSec,
                leaseRetries);
    }

    public Map reconstructMaster(String spUUID, String poolName, String masterDom,
            Map<String, String> domDict, int masterVersion, String lockPolicy, int lockRenewalIntervalSec,
            int leaseTimeSec, int ioOpTimeoutSec, int leaseRetries, int hostSpmId) {
        return getOKStatusNotImplemented();
    }

    public Map getStorageDomainStats(String sdUUID) {
        return storageService.getStorageDomainStats(sdUUID);
    }

    public Map getStorageDomainInfo(String sdUUID) {
        return storageService.getStorageDomainInfo(sdUUID);
    }

    public Map getStorageDomainsList(String spUUID, int domainType, int poolType, String path) {
        return storageService.getStorageDomainsList(spUUID, domainType, poolType, path);
    }

    public Map createVG(String sdUUID, List<String> deviceList) {
        return getOKStatusNotImplemented();
    }

    public Map createVG(String sdUUID, List<String> deviceList, boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map getVGList() {
        return getOKStatusNotImplemented();
    }

    public Map getVGInfo(String vgUUID) {
        return getOKStatusNotImplemented();
    }

    public Map getDeviceList(int storageType) {
        return getOKStatusNotImplemented();
    }

    public Map getDeviceInfo(String devGUID) {
        return getOKStatusNotImplemented();
    }

    public Map getDevicesVisibility(List<String> devicesList) {
        return getOKStatusNotImplemented();
    }

    public Map discoverSendTargets(Map<String, String> args) {
        return getOKStatusNotImplemented();
    }

    public Map getSessionList() {
        return getOKStatusNotImplemented();
    }

    public Map spmStart(String spUUID,
            int prevID,
            String prevLVER,
            int recoveryMode,
            String SCSIFencing,
            int maxHostId,
            String storagePoolFormatType) {
        return storageService.spmStart(spUUID, prevID + "", prevLVER, recoveryMode + "", SCSIFencing);
    }

    public Map spmStop(String spUUID) {
        return storageService.spmStop(spUUID);
    }

    public Map getSpmStatus(String spUUID) {
        return storageService.getSpmStatus(spUUID);
    }

    public Map fenceSpmStorage(String spUUID, int prevID, String prevLVER) {
        return getOKStatusNotImplemented();
    }

    public Map refreshStoragePool(String spUUID, String msdUUID, int masterVersion) {
        return storageService.refreshStoragePool(spUUID, msdUUID, masterVersion);
    }

    public Map getTaskStatus(String taskUUID) {
        return  taskService.getTaskStatus(taskUUID);
    }

    public Map getAllTasksStatuses() {
        return  taskService.getAllTasksStatuses();
    }

    public Map getTaskInfo(String taskUUID) {
        return getOKStatusNotImplemented();
    }

    public Map getAllTasksInfo() {
        return  taskService.getAllTasksInfo();
    }

    public Map stopTask(String taskUUID) {
        return  taskService.stopTask(taskUUID);
    }

    public Map clearTask(String taskUUID) {
        return  taskService.clearTask(taskUUID);
    }

    public Map revertTask(String taskUUID) {
        return  taskService.revertTask(taskUUID);
    }

    Map hotplugDisk(Map info) {
        return getOKStatusNotImplemented();
    }

    Map hotunplugDisk(Map info) {
        return getOKStatusNotImplemented();
    }

    public Map hotplugNic(Map innerMap) {
        return getOKStatusNotImplemented();
    }

    public Map hotunplugNic(Map innerMap) {
        return getOKStatusNotImplemented();
    }

    public Map vmUpdateDevice(String vmId, Map device) {
        return getOKStatusNotImplemented();
    }

    Map snapshot(String vmId, List<Map> snapParams) {
        return getOKStatusNotImplemented();
    }

    // Gluster vdsm commands
    public Map glusterVolumeCreate(String volumeName,
            List<String> brickList,
            int replicaCount,
            int stripeCount,
            List<String> transportList) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeSet(String volumeName, String key, String value) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeStart(String volumeName, Boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeStop(String volumeName, Boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeDelete(String volumeName) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeReset(String volumeName, String volumeOption, Boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeSetOptionsList() {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeRemoveBrickForce(String volumeName,
            List<String> brickDirectories,
            int replicaCount) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeBrickAdd(String volumeName,
            List<String> bricks,
            int replicaCount,
            int stripeCount) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeRebalanceStart(String volumeName, Boolean fixLayoutOnly, Boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map replaceGlusterVolumeBrickStart(String volumeName, String existingBrickDir, String newBrickDir) {
        return getOKStatusNotImplemented();
    }

    public Map glusterHostRemove(String hostName, Boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeReplaceBrickStart(String volumeName, String existingBrickDir, String newBrickDir) {
        return getOKStatusNotImplemented();
    }

    public Map glusterHostAdd(String hostName) {
        return getOKStatusNotImplemented();
    }

    public Map glusterHostsList() {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumesList() {
        return getOKStatusNotImplemented();
    }

    public Map ping() {
        return getOKStatusNotImplemented();
    }

    public Map diskReplicateStart(String vmUUID, Map srcDisk, Map dstDisk) {
        return getOKStatusNotImplemented();
    }

    public Map diskReplicateFinish(String vmUUID, Map srcDisk, Map dstDisk) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeProfileStart(String volumeName) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeProfileStop(String volumeName) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeStatus(String volumeName, String brickName, String volumeStatusOption) {
        return getOKStatusNotImplemented();
    }

    public Map glusterVolumeProfileInfo(String volumeName) {
        return getOKStatusNotImplemented();
    }

    /** IRS **/
    public Map createVolume(String sdUUID, String spUUID, String imgGUID, int size, int volFormat,
            int volType, int diskType, String volUUID, String descr, String srcImgGUID, String srcVolUUID) {
        return storageService.createVolume(
                sdUUID,
                spUUID,
                imgGUID,
                size + "",
                volFormat,
                volType,
                diskType,
                volUUID,
                descr,
                srcImgGUID,
                srcVolUUID);
    }

    public Map createVolume(String sdUUID, String spUUID, String imgGUID, String size, int volFormat,
            int volType, int diskType, String volUUID, String descr, String srcImgGUID, String srcVolUUID) {
        return storageService.createVolume(
                sdUUID,
                spUUID,
                imgGUID,
                size,
                volFormat,
                volType,
                diskType,
                volUUID,
                descr,
                srcImgGUID,
                srcVolUUID);
    }

    public Map copyImage(String sdUUID, String spUUID, String vmGUID, String srcImgGUID,
            String srcVolUUID, String dstImgGUID, String dstVolUUID, String descr) {
        return getOKStatusNotImplemented();
    }

    public Map copyImage(String sdUUID, String spUUID, String vmGUID, String srcImgGUID,
            String srcVolUUID, String dstImgGUID, String dstVolUUID, String descr, String dstSdUUID, int volType,
            int volFormat, int preallocate, String postZero, String force) {
        return getOKStatusNotImplemented();
    }

    public Map setVolumeDescription(String sdUUID, String spUUID, String imgGUID, String volUUID,
            String description) {
        return getOKStatusNotImplemented();
    }

    public Map mergeSnapshots(String sdUUID, String spUUID, String vmGUID, String imgGUID,
            String ancestorUUID, String successorUUID) {
        return getOKStatusNotImplemented();
    }

    public Map mergeSnapshots(String sdUUID, String spUUID, String vmGUID, String imgGUID,
            String ancestorUUID, String successorUUID, String postZero) {
        return getOKStatusNotImplemented();
    }

    public Map deleteVolume(String sdUUID, String spUUID, String imgGUID, List<String> volUUID,
            String postZero) {
        return getOKStatusNotImplemented();
    }

    public Map deleteVolume(String sdUUID, String spUUID, String imgGUID, List<String> volUUID,
            String postZero, String force) {
        return getOKStatusNotImplemented();
    }

    public Map getVolumeInfo(String sdUUID, String spUUID, String imgGUID, String volUUID) {
        return storageService.getVolumeInfo(sdUUID, spUUID, imgGUID, volUUID);
    }

    public Map getStats() {
        return getOKStatusNotImplemented();
    }

    public Map exportCandidate(String sdUUID, String vmGUID, List<String> volumesList, String vmMeta,
            String templateGUID, String templateVolGUID, String templateMeta, String expPath, String collapse,
            String force) {
        return getOKStatusNotImplemented();
    }

    public Map importCandidate(String sdUUID, String vmGUID, String templateGUID,
            String templateVolGUID, String path, String type, String force) {
        return getOKStatusNotImplemented();
    }

    public Map getIsoList(String spUUID) {
        return storageService.getIsoList(spUUID);
    }

    public Map getFloppyList(String spUUID) {
        return storageService.getFloppyList(spUUID);
    }

    public Map extendVolume(String sdUUID, String spUUID, String imgGUID, String volUUID, int newSize) {
        return getOKStatusNotImplemented();
    }

    public Map activateStorageDomain(String sdUUID, String spUUID) {
        return storageService.activateStorageDomain(sdUUID, spUUID);
    }

    public Map deactivateStorageDomain(String sdUUID, String spUUID, String msdUUID, int masterVersion) {
        return storageService.deactivateStorageDomain(sdUUID, spUUID, msdUUID, masterVersion);
    }

    public Map detachStorageDomain(String sdUUID, String spUUID, String msdUUID, int masterVersion) {
        return storageService.detachStorageDomain(sdUUID, spUUID, msdUUID, masterVersion);
    }

    public Map forcedDetachStorageDomain(String sdUUID, String spUUID) {
        return getOKStatusNotImplemented();
    }

    public Map attachStorageDomain(String sdUUID, String spUUID) {
        return storageService.attachStorageDomain(sdUUID, spUUID);
    }

    public Map setStorageDomainDescription(String sdUUID, String description) {
        return getOKStatusNotImplemented();
    }

    public Map reconstructMaster(String spUUID, int hostSpmId, String msdUUID, String masterVersion) {
        return getOKStatusNotImplemented();
    }

    public Map extendStorageDomain(String sdUUID, String spUUID, List<String> devlist) {
        return getOKStatusNotImplemented();
    }

    public Map extendStorageDomain(String sdUUID, String spUUID, List<String> devlist, boolean force) {
        return getOKStatusNotImplemented();
    }

    public Map setStoragePoolDescription(String spUUID, String description) {
        return getOKStatusNotImplemented();
    }

    public Map getStoragePoolInfo(String spUUID) {
        return storageService.getStoragePoolInfo(spUUID);
    }

    public Map destroyStoragePool(String spUUID, int hostSpmId, String SCSIKey) {
        return getOKStatusNotImplemented();
    }

    public Map deleteImage(String sdUUID, String spUUID, String imgGUID, String postZero) {
        return getOKStatusNotImplemented();
    }

    public Map deleteImage(String sdUUID, String spUUID, String imgGUID, String postZero, String force) {
        return getOKStatusNotImplemented();
    }

    public Map moveImage(String spUUID, String srcDomUUID, String dstDomUUID, String imgGUID,
            String vmGUID, int op, String postZero, String force) {
        return getOKStatusNotImplemented();
    }

    public Map moveImage(String spUUID, String srcDomUUID, String dstDomUUID, String imgGUID,
            String vmGUID, int op) {
        return getOKStatusNotImplemented();
    }

    public Map cloneImageStructure(String spUUID, String srcDomUUID, String imgGUID, String dstDomUUID) {
        return getOKStatusNotImplemented();
    }

    public Map syncImageData(String spUUID, String srcDomUUID, String imgGUID, String dstDomUUID, String syncType) {
        return getOKStatusNotImplemented();
    }

    public Map getImageDomainsList(String spUUID, String imgUUID) {
        return getOKStatusNotImplemented();
    }

    public Map setMaxHosts(int maxHosts) {
        return getOKStatusNotImplemented();
    }

    public Map updateVM(String spUUID, List<Map> vms) {
        return getOKStatusNotImplemented();
    }

    public Map removeVM(String spUUID, String vmGUID) {
        return getOKStatusNotImplemented();
    }

    public Map updateVM(String spUUID, List<Map> vms, String StorageDomainId) {
        return getOKStatusNotImplemented();
    }

    public Map removeVM(String spUUID, String vmGUID, String storageDomainId) {
        return getOKStatusNotImplemented();
    }

    public Map getVmsInfo(String storagePoolId, String storageDomainId, List<String> VMIDList) {
        return getOKStatusNotImplemented();
    }

    public Map getVmsList(String storagePoolId, String storageDomainId) {
        return getOKStatusNotImplemented();
    }

    public Map upgradeStoragePool(String storagePoolId, String targetVersion) {
        return getOKStatusNotImplemented();
    }

    public Map getImagesList(String sdUUID) {
        return getOKStatusNotImplemented();
    }

    public Map getVolumesList(String sdUUID, String spUUID, String imgUUID) {
        return getOKStatusNotImplemented();
    }

    public Map getFileList(String spUUID) {
        return storageService.getFileList(spUUID);
    }

}
