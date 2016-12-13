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

import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Collection;

import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.Utils;
import org.ovirt.vdsmfake.domain.Device;
import org.ovirt.vdsmfake.domain.Host;
import org.ovirt.vdsmfake.domain.VM;
import org.ovirt.vdsmfake.rpc.json.JsonRpcNotification;
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
    private final static List fullListMapKeys = Arrays.asList("username fqdn acpiEnable emulatedMachine pid transparentHugePages keyboardLayout displayPort displaySecurePort timeOffset cpuType pauseCode nicModel smartcardEnable kvmEnable pitReinjection smp vmType displayIp clientIp smpCoresPerSocket nice".split(" "));
    public Map list(boolean fullStatus, List vmList) {
        final Host host = getActiveHost();

        final Map resultMap = getDoneStatus();

        final List statusList = new ArrayList();
        for (VM vm : host.getRunningVMs().values()) {
            if (!vmList.isEmpty() && !vmList.contains(vm.getId())) {
                continue;
            }

            Map vmMap = null;
            if (fullStatus) {
                vmMap = VMInfoService.getInstance().getFromKeys(vm, fullListMapKeys);
                vmMap.put("status", vm.getStatus().toString()); // Up
                vmMap.put("vmId", vm.getId()); // 4c36aca1-577f-4533-987d-a8288faab149
                Map customMap = vm.getCustomMap();
                if( customMap != null ) {
                    vmMap.put("custom", vm.getCustomMap());
                }
                vmMap.put("devices", vm.getDeviceList());
                vmMap.put("memSize", vm.getMemSize());
                vmMap.put("vmName", vm.getName()); // Fedora17_test1
                vmMap.put("display", vm.getDisplayType());
            }
            else {
                vmMap = map();
                vmMap.put("status", vm.getStatus().toString()); // Up
                vmMap.put("vmId", vm.getId()); // 4c36aca1-577f-4533-987d-a8288faab149
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

        Map<String, Object> resultMap = map();

        Map statusMap = map();
        statusMap.put("message", success ? "Migration process starting" : "VM not found");
        statusMap.put("code", (success ? "0" : "100"));

        resultMap.put("status", statusMap);

        log.info("Migrating VM {} from host: {} to: {}", new Object[] { vm.getId(), vm.getHost().getName(), targetHost.getName() });

        return resultMap;
    }
    private final static List VmStatsKeys = Arrays.asList("username fqdn memUage balloonInfo username acpiEnable pid displayIp displayPort session displaySecurePort timeOffset hash pauseCode kvmEnable monitorResponse statsAge elapsedTime vmType cpuSys appsList guestIPs".split(" "));
    public Map getVmStats(String uuid) {

        final Host host = getActiveHost();
        Map resultMap = getDoneStatus();
        List statusList = new ArrayList();

        VM vm = host.getRunningVMs().get(uuid);
        if (vm != null){
            statusList.add(fillVmStatsMap(vm));
        }

        resultMap.put("statsList", statusList);

        Utils.getLatency();

        return resultMap;
    }

    List<Map<String, Object>> getNetworkInterfaces(VM vm){

        Map<String, Object> resultMap = new HashMap();
        List<String> inet6Addresses =  new ArrayList<>();
        List<String> inet4Addresses =  new ArrayList<>();
        List<Map<String, Object>> nets = new ArrayList<>();

        resultMap.put("name", "eth0");

        inet6Addresses.add("fe80::21a:4aff:fe16:2016");
        inet6Addresses.add("2620:52:0:2380:21a:4aff:fe16:2016");

        resultMap.put("inet6", inet6Addresses);

        inet4Addresses.add(vm.getIp());
        resultMap.put("inet", inet4Addresses);
        resultMap.put("hw", vm.getMacAddress());

        nets.add(resultMap);

        log.debug("network list is {}", nets.toString());
        return nets;
    }

    Map getNetworkStatsMap(VM vm) {
        List<Device> nicDevices = vm.getDevicesByType(Device.DeviceType.NIC);

        String macAddress = vm.getMacAddress();
        if( macAddress.equals(VM.NONE_STRING) ) {
            log.debug("no mac address for vm {}", vm.getId());
            return map();
        }

        Map resultMap = map();
        int count = 0;
        for(Device device : nicDevices)
        {
            ArrayList loadValues = AppConfig.getInstance().getNetworkLoadValues();
            Map netStats = map();
            String dName = "vnet" + count;

            netStats.put("txErrors", "0");
            netStats.put("state", "unknown");
            netStats.put("macAddr", device.getMacAddr());
            netStats.put("name", dName);
            netStats.put("txDropped", "0");
            netStats.put("txRate", Utils.rangeParsser(loadValues));
            netStats.put("rxErrors", "0");
            netStats.put("rxRate",  Utils.rangeParsser(loadValues));
            netStats.put("tx",  Utils.rangeParsser(loadValues));
            netStats.put("rx",  Utils.rangeParsser(loadValues));
            netStats.put("rxDropped", "0");
            netStats.put("speed", "1000");
            netStats.put("sampleTime", "4318787.08");

            resultMap.put(dName, netStats);
            ++count;
        }
        return resultMap;
    }

    Map getVMDisksMap(VM vm) {
        Map resultMap = map();

        List<Device> diskDevices = vm.getDevicesByType(Device.DeviceType.DISK);

        List values_ = Arrays.asList("a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" "));
        LinkedList<String> values = new LinkedList<String>();
        values.addAll(values_);

        for(Device disk : diskDevices) {
            if( values.isEmpty() ) {
                break;
            }
            Map diskMap = map();
            diskMap.put("readLatency", "0");
            diskMap.put("apparentsize", "197120");
            diskMap.put("writeLatency", "0");
            diskMap.put("imageID", disk.getImageID());
            diskMap.put("flushLatency", "0");
            diskMap.put("readRate", "0");
            diskMap.put("truesize", "139264");
            diskMap.put("writeRate", "0.00");

            resultMap.put("vd" + values.get(0), diskMap);
            values.remove(0);
        }

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

    private final static List VmConfInfoKeys = Arrays.asList("acpiEnable vmType guestName guestOS kvmEnable pauseCode displayIp displayPort displaySecurePort pid".split(" "));
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
        AppConfig appConfig = AppConfig.getInstance();
        Map vmStatMap = VMInfoService.getInstance().getFromKeys(vm, VmStatsKeys);
        vmStatMap.put("status", vm.getStatus().toString());

        // ip validation if no exist set ip, for vms which already registered in the setup
        if (vm.getIp() == null || vm.getIp().equals("0.0.0.0") || vm.getIp().isEmpty() || vm.getIp().equals("?")){
            vm.setIp(Utils.ipGenerator());
        }

        //missing data
        Map dis = map();
        ArrayList display = new ArrayList();
        dis.put("tlsPort", "5900");
        dis.put("ipAddress", vm.getIp());
        dis.put("type", "spice");
        dis.put("port", "-1");
        display.add(dis);
        vmStatMap.put("displayInfo", display);

        vmStatMap.put("pid", "1111");
        vmStatMap.put("session", "Unknown");
        vmStatMap.put("timeOffset", "0");
        vmStatMap.put("pauseCode", "NOERR");

        Map ballon = map();
        //TODO: compute 10% from the actual mem for ballooning.
        ballon.put("balloon_max", "2048");
        ballon.put("balloon_min", "1024");
        ballon.put("balloon_target", "2048");
        ballon.put("balloon_cur", "512");

        vmStatMap.put("balloonInfo", ballon);

        vmStatMap.put("guestIPs", vm.getIp());
        vmStatMap.put("guestName", "localhost.localdomain");
        vmStatMap.put("guestFQDN", "localhost.localdomain");
        vmStatMap.put("guestOs", "2.6.32-642.el6.x86_64");
        vmStatMap.put("guestOsInfo", getGuestOsInto());
        vmStatMap.put("guestCPUCount", "1");
        vmStatMap.put("guestTimezone", getGuestTimeZone());

        //cpu
        vmStatMap.put("cpuSys", Utils.rangeParsser(appConfig.getCpuLoadValues()));
        vmStatMap.put("cpuLoad", Utils.rangeParsser(appConfig.getCpuLoadValues()));
        vmStatMap.put("cpuUser", Utils.rangeParsser(appConfig.getCpuLoadValues()));

        //memory
        vmStatMap.put("memUsage", Utils.rangeParsser(appConfig.getMemLoadValues()));


        //network
        vmStatMap.put("netIfaces", getNetworkInterfaces(vm));
        Map network = getNetworkStatsMap(vm);
        if( !network.isEmpty() ) {
            vmStatMap.put("network", network);
        }

        Map disks = getVMDisksMap(vm);
        if( !disks.isEmpty() ) {
            vmStatMap.put("disks", disks);
        }
        vmStatMap.put("elapsedTime", vm.getElapsedTimeInSeconds());


        vmStatMap.put("vcpuCount", "1");
        vmStatMap.put("clientIp", "");
        vmStatMap.put("hash", Integer.toString(vm.hashCode()));
        vmStatMap.put("vmType", "kvm");
        vmStatMap.put("vmId", vm.getId());
        vmStatMap.put("displayIp", vm.getIp());
        vmStatMap.put("vcpuPeriod", 100000);
        vmStatMap.put("displayPort", "-1");
        vmStatMap.put("vcpuQuota", "-1");
        vmStatMap.put("kvmEnable", "true");
        vmStatMap.put("monitorResponse", "0");
        vmStatMap.put("statsAge", "2.46");
        vmStatMap.put("username", "None");
        vmStatMap.put("lastLogin", 1426169218.410367);
        ArrayList emptylist = new ArrayList();
        vmStatMap.put("ioTune", emptylist);
        vmStatMap.put("displaySecurePort", "5900");
        vmStatMap.put("vmJobs", map());

        Map memstats = map();
        if (!vmStatMap.get("memUsage").toString().isEmpty()) {
            memstats.put("swap_out", "0");
            memstats.put("majflt", "0");
            memstats.put("swap_usage", "0");
            memstats.put("swap_total", "0");
            memstats.put("swap_in", "0");
            memstats.put("mem_free", Integer.toString(vm.getMemSize() - Integer.valueOf(vmStatMap.get("memUsage").toString())));
            memstats.put("pageflt", "131");
            memstats.put("mem_total", Integer.toString(vm.getMemSize()));
            memstats.put("mem_unused", memstats.get("mem_free"));

            vmStatMap.put("memoryStats", memstats);
        }

        //adding app list
        ArrayList applist = new ArrayList();
        applist.add("kernel-2.6.32-431.el6");
        applist.add("rhevm-guest-agent-common-1.0.9-1.el6ev");
        vmStatMap.put("appsList", applist);

        vmStatMap.put("displayType", "qxl");

        return vmStatMap;
    }

    public Map getAllVmStats() {
        final Host host = getActiveHost();

        Map resultMap = getDoneStatus();

        // iterate vms

        List statusList = host.getRunningVMs().values().stream()
                .map(this::fillVmStatsMap)
                .collect(Collectors.toList());

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

        Map statusMap = map();

        addTask(TaskType.SHUTDOWN_VM, 5000l, vm);

        statusMap.put("message", "Machine destroyed");
        statusMap.put("code", "0");

        resultMap.put("status", statusMap);

        return resultMap;
    }

    public Map shutdown(String vmId, String timeout, String message) {
        final Map resultMap = getStatusMap("Machine shut down", 0);

        final VM vm = getActiveHost().getRunningVMs().get(vmId);
        if (vm != null) {
            vm.setStatus(VM.VMStatus.PoweringDown);
        }

        addTask(TaskType.SHUTDOWN_VM, 5000l, vm);

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
            vm.setIp(Utils.ipGenerator());


            Integer memSize = 0;
            Object boxedMemSize = vmParams.get("memSize");
            if(boxedMemSize instanceof String) {
                memSize = Integer.parseInt((String) boxedMemSize);
            }
            else {
                memSize = (Integer)boxedMemSize;
            }
            vm.setMemSize(memSize);

            final Object[] devices = (Object[]) vmParams.get("devices");
            vm.setDeviceList(devices == null ? new ArrayList() : Arrays.asList(devices));
            Map custom = (Map) vmParams.get("custom");
            vm.setCustomMap(custom != null ? custom : map());

            // append address tag when missing by the device
            vm.generateDevicesAddressIfMissing();

            // convert Maps to important Device objects
            vm.parseDevices();

            host.getRunningVMs().put(vm.getId(), vm);
            // persist
            updateHost(host);

            final Map resultMap = getDoneStatus();

            vmParams.put("status", vm.getStatus().toString()); // WaitForLaunch
            resultMap.put("vmList", vmParams);

            log.debug("VM {} created on host {}", vmId, host.getName());

            addTask(TaskType.START_VM, 2000l, vm);
            addTask(TaskType.START_VM_AS_UP, 5000l, vm);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    public void addTask(TaskType type, long delay, Object object) {
        // support events, if enabled.
        try {
            if (AppConfig.getInstance().isJsonEvents()){
                new JsonRpcNotification().fireEvents(type, delay, object);
            } else {
                //backward compatible.
                TaskProcessor.getInstance().addTask(new TaskRequest(type, delay, object));
            }
        } catch (Exception e) {
            log.error("wrapping task error:", e);
        }
    }

    private Map<String, String> getGuestOsInto(){
        Map<String, String> resultMap = map();
        resultMap.put("kernel","2.6.32-642.el6.x86_64");
        resultMap.put("type", "linux");
        resultMap.put("version", "6.7");
        resultMap.put("arch", "x86_64");
        resultMap.put("codename", "Santiago");
        resultMap.put("distribution", "Red Hat Enterprise Linux Server");
        return resultMap;
    }

    private Map<String, String> getGuestTimeZone(){
        Map<String, String> resultMap = map();
        resultMap.put("zone","Israel");
        resultMap.put("offset", "120");
        return resultMap;
    }
}
