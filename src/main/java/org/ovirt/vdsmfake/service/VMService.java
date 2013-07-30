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
package org.ovirt.vdsmfake.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.ovirt.vdsmfake.domain.Host;
import org.ovirt.vdsmfake.domain.VM;
import org.ovirt.vdsmfake.task.TaskProcessor;
import org.ovirt.vdsmfake.task.TaskRequest;
import org.ovirt.vdsmfake.task.TaskType;

/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class VMService extends AbstractService {

    final static VMService instance = new VMService();

    public static VMService getInstance() {
        return instance;
    }

    public VMService() {
    }

    public Map list() {
        final Host host = getActiveHost();

        final Map resultMap = getDoneStatus();

        final List statusList = new ArrayList();
        for (VM vm : host.getRunningVMs().values()) {
            Map vmMap = map();
            vmMap.put("status", vm.getStatus().toString()); // Up
            vmMap.put("vmId", vm.getId()); // 4c36aca1-577f-4533-987d-a8288faab149
            statusList.add(vmMap);
        }

        resultMap.put("vmList", statusList);

        return resultMap;
    }

    public Map list(String fullStatus, List vmList) {
        final Host host = getActiveHost();

        final Map resultMap = getDoneStatus();

        final List statusList = new ArrayList();
        for (VM vm : host.getRunningVMs().values()) {
            if (!vmList.contains(vm.getId())) {
                continue;
            }

            Map vmMap = map();
            vmMap.put("status", vm.getStatus().toString()); // Up
            vmMap.put("vmId", vm.getId()); // 4c36aca1-577f-4533-987d-a8288faab149

            if ("true".equals(fullStatus)) {
                vmMap.put("acpiEnable", "true");
                vmMap.put("emulatedMachine", "pc-0.14");
                vmMap.put("pid", "10294");
                vmMap.put("transparentHugePages", "true");
                vmMap.put("keyboardLayout", "en-us");
                vmMap.put("displayPort", "5902");
                vmMap.put("displaySecurePort", "-1");
                vmMap.put("timeOffset", "0");
                vmMap.put("cpuType", vm.getCpuType());
                vmMap.put("custom", vm.getCustomMap());
                vmMap.put("pauseCode", "NOERR");
                vmMap.put("nicModel", "rtl8139,pv");
                vmMap.put("smartcardEnable", "false");
                vmMap.put("kvmEnable", "true");
                vmMap.put("pitReinjection", "false");
                vmMap.put("devices", vm.getDeviceList());
                vmMap.put("smp", "1");
                vmMap.put("vmType", "kvm");
                vmMap.put("memSize", vm.getMemSize());
                vmMap.put("displayIp", "0");
                vmMap.put("clientIp", "");
                vmMap.put("smpCoresPerSocket", "1");
                vmMap.put("vmName", vm.getName()); // Fedora17_test1
                vmMap.put("display", vm.getDisplayType());
                vmMap.put("nice", "0");
             }

            statusList.add(vmMap);
        }

        resultMap.put("vmList", statusList);

        return resultMap;
    }


    public Map migrate(Map request) {
        // String method = (String) request.get("method"); // online
        String dst = (String)request.get("dst"); // 10.34.63.178:54321
        // String src = (String)request.get("src"); // 10.34.63.177
        String vmId = (String)request.get("vmId"); // 79567083-9889-4bcc-90e3-291885b0da7f

        boolean success = true;

        final VM vm = getActiveHost().getRunningVMs().get(vmId);
        if (vm == null) {
            log.info("VM not found: " + vmId);
            throw new RuntimeException("VM not found: " + vmId);
        }

        // bind clone of VM to the target host
        VM targetVM = vm.clone();
        vm.setStatus(VM.VMStatus.MigratingFrom);
        targetVM.setStatus(VM.VMStatus.MigratingTo);

        String targetServerName = dst;

        // get target server
        int idx = dst.indexOf(':');
        if(idx != -1) {
            targetServerName = dst.substring(0, idx);
        }

        final Host targetHost = getHostByName(targetServerName);
        if (targetHost == null) {
            log.info("Target host not found: " + dst);
            throw new RuntimeException("Target host not found: " + dst + ", name: " + targetServerName);
        }
        targetVM.setHost(targetHost);
        targetHost.getRunningVMs().put(targetVM.getId(), targetVM);

        // add asynch task
        TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.FINISH_MIGRATED_FROM_VM, 10000l, vm));
        TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.FINISH_MIGRATED_TO_VM, 10000l, targetVM));
        // plan next status task
        TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.FINISH_MIGRATED_FROM_VM_REMOVE_FROM_HOST, 20000l, vm));

        Map resultMap = map();

        Map statusMap = map();
        statusMap.put("message", success ? "Migration process starting" : "VM not found");
        statusMap.put("code", Integer.valueOf(success ? 0 : 100));

        resultMap.put("status", statusMap);

        log.info("Migrating VM {} from host: {} to: {}", new Object[] { vm.getId(), vm.getHost().getName(), targetHost.getName() });

        return resultMap;
    }

    public Map getVmStats(String uuid) {
        final Host host = getActiveHost();

        Map resultMap = getDoneStatus();

        List statusList = new ArrayList();

        VM vm = host.getRunningVMs().get(uuid);

        if (vm != null) {
            Map vmStatMap = map();
            vmStatMap.put("status", vm.getStatus().toString());
            vmStatMap.put("memUsage", getRandomNum(2));
            vmStatMap.put("username", "Unknown");
            vmStatMap.put("acpiEnable", "true");
            vmStatMap.put("pid", "29410");
            vmStatMap.put("displayIp", "0");
            vmStatMap.put("displayPort", "5900");
            vmStatMap.put("session", "Unknown");
            vmStatMap.put("displaySecurePort", "-1");
            vmStatMap.put("timeOffset", "0");
            vmStatMap.put("hash", getRandomNum(20)); // 3077163634575265748
            vmStatMap.put("balloonInfo", getBalloonInfoMap());
            vmStatMap.put("pauseCode", "NOERR");
            vmStatMap.put("kvmEnable", "true");
            vmStatMap.put("network", getNetworkStatsMap(vm));
            vmStatMap.put("vmId", vm.getId());
            vmStatMap.put("displayType", vm.getDisplayType());
            vmStatMap.put("cpuUser", "0." + getRandomNum(2));
            vmStatMap.put("disks", getVMDisksMap(vm));
            vmStatMap.put("monitorResponse", "0");
            vmStatMap.put("statsAge", "0.15");
            vmStatMap.put("elapsedTime", vm.getElapsedTimeInSeconds());
            vmStatMap.put("vmType", "kvm");
            vmStatMap.put("cpuSys", "0." + getRandomNum(2));
            vmStatMap.put("appsList", lst());
            vmStatMap.put("guestIPs", ""); // null

            statusList.add(vmStatMap);
        }

        resultMap.put("statsList", statusList);

        return resultMap;
    }

    Map getBalloonInfoMap() {
        Map resultMap = map();
        resultMap.put("balloon_max", Integer.valueOf(524288));
        resultMap.put("balloon_cur", Integer.valueOf(524288));

        return resultMap;
    }

    Map getNetworkStatsMap(VM vm) {
        Map resultMap = map();

        Map netStats = map();

        resultMap.put("vnet0", netStats);

        netStats.put("txErrors", "0");
        netStats.put("state", "unknown");
        netStats.put("macAddr", vm.getMacAddress()); // 00:1a:4a:16:01:51
        netStats.put("name", "vnet0");
        netStats.put("txDropped", "0");
        netStats.put("txRate", "0.0");
        netStats.put("rxErrors", "0");
        netStats.put("rxRate", "0.0");
        netStats.put("rxDropped", "0");

        return resultMap;
    }

    Map getVMDisksMap(VM vm) {
        Map resultMap = map();

        Map disk1Map = map();
        Map disk2Map = map();

        disk1Map.put("vda", disk1Map);
        disk1Map.put("hdc", disk2Map);

        disk1Map.put("readLatency", "0");
        disk1Map.put("apparentsize", "197120");
        disk1Map.put("writeLatency", "0");
        disk1Map.put("imageID", vm.getImageId());
        disk1Map.put("flushLatency", "0");
        disk1Map.put("readRate", "0");
        disk1Map.put("truesize", "139264");
        disk1Map.put("writeRate", "0.00");

        disk2Map.put("readLatency", "0");
        disk2Map.put("apparentsize", "0");
        disk2Map.put("writeLatency", "0");
        disk2Map.put("flushLatency", "0");
        disk2Map.put("readRate", "0");
        disk2Map.put("truesize", "0");
        disk2Map.put("writeRate", "0.00");

        return resultMap;
    }

    private Collection<VM> getVmListFromIds(List vmIds) {

        final Host host = getActiveHost();

        if( vmIds == null || vmIds.isEmpty() ) {
            return host.getRunningVMs().values();
        }

        ArrayList<VM> vmList = new ArrayList<VM>();
        for( Object id : vmIds ) {
            if( host.getRunningVMs().containsKey(id) ) {
                vmList.add( host.getRunningVMs().get(id) );
            }
        }

        return vmList;
    }

    private Map extractKeysFromVmAndHash(VM vm, List keys, Map stats) {
        if( stats == null ) {
            stats = fillVmStatsMap(vm);
        }
        Map result = map();
        for( Object key : keys ) {
            if( stats.containsKey(key) ) {
                result.put(key, stats.get(key));
            }
        }
        result.put("hashes", getRuntimeStatsHashesForVm(vm, stats));
        return result;
    }

    private Map extractKeysFromVm(VM vm, List keys, Map stats) {
        if( stats == null ) {
            stats = fillVmStatsMap(vm);
        }
        Map result = map();
        for( Object key : keys ) {
            if( stats.containsKey(key) ) {
                result.put(key, stats.get(key));
            }
        }
        return result;
    }

    private Map getExtractedStatsAndHash(List keys, List vmIds, boolean hashes) {
        final Host host = getActiveHost();
        Map result = map();
        for (VM vm : getVmListFromIds(vmIds)) {
            Map stats = null;
            if( hashes ) {
                stats = extractKeysFromVmAndHash(vm, keys, fillVmStatsMap(vm));
            }
            else {
                stats = extractKeysFromVm(vm, keys, fillVmStatsMap(vm));
            }
            result.put(vm.getId(), stats);
        }
        return result;
    }

    private Map getExtractedStats(List keys, List vmIds) {
        return getExtractedStatsAndHash(keys, vmIds, false);
    }

    private Map getRuntimeStatsHashesForVm(VM vm, Map stats)
    {
        Map hashes = map();
        Object hash = "0";
        if( stats.containsKey("hash") ) {
            hash = stats.get("hash");
        }
        hashes.put("config", hash);
        hashes.put("info", "" + extractKeysFromVm(vm, VmConfInfoKeys, stats).hashCode());
        hashes.put("status", "" + extractKeysFromVm(vm, VmStatusKeys, stats).hashCode());
        hashes.put("guestDetails", "" + extractKeysFromVm(vm, VmGuestDetailsKeys, stats).hashCode());
        return hashes;
    }

    private final static List VmRuntimeStatsKeys = Arrays.asList("cpuSys cpuUser memUsage elapsedTime status statsAge".split(" "));
    public Map getAllVmRuntimeStats() {
        Map resultMap = getDoneStatus();
        resultMap.put("runtimeStats", getExtractedStatsAndHash(VmRuntimeStatsKeys, null, true));
        return resultMap;
    }

    private final static List VmDeviceStatsKeys = Arrays.asList("network disks disksUsage balloonInfo memoryStats".split(" "));
    public Map getAllVmDeviceStats() {
        Map resultMap = getDoneStatus();
        resultMap.put("deviceStats", getExtractedStats(VmDeviceStatsKeys, null));
        return resultMap;
    }

    private final static List VmStatusKeys = Arrays.asList("timeOffset monitorResponse clientIp lastLogin username session guestIps".split(" "));
    public Map getVmStatus(List vmIds) {
        Map resultMap = getDoneStatus();
        resultMap.put("vmStatus", getExtractedStats(VmStatusKeys, vmIds));
        return resultMap;
    }

    private final static List VmConfInfoKeys = Arrays.asList("timeOffset monitorResponse clientIp lastLogin username session guestIps".split(" "));
    public Map getVmConfInfo(List vmIds) {
        Map resultMap = getDoneStatus();
        resultMap.put("vmConfInfo", getExtractedStats(VmConfInfoKeys, vmIds));
        return resultMap;
    }

    private final static List VmGuestDetailsKeys = Arrays.asList("appsList netIfaces".split(" "));
    public Map getVmGuestDetails(List vmIds) {
        Map resultMap = getDoneStatus();
        resultMap.put("guestDetails", getExtractedStats(VmGuestDetailsKeys, vmIds));
        return resultMap;
    }

    private Map fillVmStatsMap(VM vm)
    {
        Map vmStatMap = map();
        vmStatMap.put("status", vm.getStatus().toString());
        vmStatMap.put("memUsage", "0");
        vmStatMap.put("username", "Unknown");
        vmStatMap.put("acpiEnable", "true");
        vmStatMap.put("pid", "29410");
        vmStatMap.put("displayIp", "0");
        vmStatMap.put("displayPort", "5900");
        vmStatMap.put("session", "Unknown");
        vmStatMap.put("displaySecurePort", "-1");
        vmStatMap.put("timeOffset", "0");
        vmStatMap.put("hash", getRandomNum(20)); // 3077163634575265748
        vmStatMap.put("balloonInfo", getBalloonInfoMap());
        vmStatMap.put("pauseCode", "NOERR");
        vmStatMap.put("kvmEnable", "true");
        vmStatMap.put("network", getNetworkStatsMap(vm));
        vmStatMap.put("vmId", vm.getId());
        vmStatMap.put("displayType", "qxl");
        vmStatMap.put("cpuUser", "0." + getRandomNum(2));
        vmStatMap.put("disks", getVMDisksMap(vm));
        vmStatMap.put("monitorResponse", "0");
        vmStatMap.put("statsAge", "0.15");
        vmStatMap.put("elapsedTime", vm.getElapsedTimeInSeconds());
        vmStatMap.put("vmType", "kvm");
        vmStatMap.put("cpuSys", "0." + getRandomNum(2));
        vmStatMap.put("appsList", lst());
        vmStatMap.put("guestIPs", ""); // null
        return vmStatMap;
    }

    public Map getAllVmStats() {
        final Host host = getActiveHost();

        Map resultMap = getDoneStatus();

        // iterate vms

        List statusList = new ArrayList();

        for (VM vm : host.getRunningVMs().values()) {
            statusList.add(fillVmStatsMap(vm));
        }

        resultMap.put("statsList", statusList);

        return resultMap;
    }

    public Map setVmTicket(String uuid, String password, String ttl, String existingConnAction, Map params) {
        return getDoneStatus();
    }

    public Map destroy(String vmId) {
        final VM vm = getActiveHost().getRunningVMs().get(vmId);
        if (vm == null) {
            log.info("VM not found: " + vmId);
            throw new RuntimeException("VM not found: " + vmId);
        }

        vm.setStatus(VM.VMStatus.PoweringDown);

        Map resultMap = map();

        // add async task
        TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.SHUTDOWN_VM, 5000l, vm));

        Map statusMap = map();
        statusMap.put("message", "Machine destroyed");
        statusMap.put("code", 0);

        resultMap.put("status", statusMap);

        return resultMap;
    }

    public Map shutdown(String vmId, String timeout, String message) {
        final Map resultMap = getStatusMap("Machine shut down", 0);

        final VM vm = getActiveHost().getRunningVMs().get(vmId);
        if (vm != null) {
            vm.setStatus(VM.VMStatus.PoweringDown);
        }

        // add asynch task
        TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.SHUTDOWN_VM, 5000l, vm));

        return resultMap;
    }

    public Map create(Map vmParams) {
        try {
            final Host host = getActiveHost();

            final String vmId = (String) vmParams.get("vmId");

            final VM vm = new VM();
            vm.setTimeCreated(System.currentTimeMillis());
            vm.setId(vmId);
            vm.setName((String) vmParams.get("vmName"));
            vm.setCpuType((String) vmParams.get("cpuType"));
            vm.setHost(host);
            vm.setMemSize((Integer) vmParams.get("memSize"));

            final Object[] devices = (Object[]) vmParams.get("devices");
            vm.setDeviceList(devices == null ? new ArrayList() : Arrays.asList(devices));
            vm.setCustomMap((Map) vmParams.get("custom"));

            // append address tag when missing by the device
            vm.generateDevicesAddressIfMissing();

            // convert Maps to important Device objects
            vm.parseDevices();

            host.getRunningVMs().put(vm.getId(), vm);
            // persist
            updateHost(host);

            // add asynch tasks
            TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.START_VM, 2000l, vm));
            // plan next status task
            TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.START_VM_AS_UP, 10000l, vm));

            final Map resultMap = getDoneStatus();

            vmParams.put("status", vm.getStatus().toString()); // WaitForLaunch
            resultMap.put("vmList", vmParams);

            log.info("VM {} created on host {}", vmId, host.getName());

            return resultMap;
        } catch (Exception e) {
            log.error(ERROR, e);
            throw new RuntimeException(ERROR, e);
        }
    }
}
