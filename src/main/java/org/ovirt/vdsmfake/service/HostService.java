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

import java.util.List;
import java.util.Map;

import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.domain.DataCenter;
import org.ovirt.vdsmfake.domain.Host;
import org.ovirt.vdsmfake.domain.StorageDomain;
import org.ovirt.vdsmfake.domain.VM;

/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class HostService extends AbstractService {

    final static HostService instance = new HostService();

    public static HostService getInstance() {
        return instance;
    }

    public HostService() {
    }

    public Map getVdsCapabilities() {
        final Host host = getActiveHost();

        try {
            Map resultMap = getDoneStatus();

            Map infoMap = map();
            infoMap.put("HBAInventory", getHBAInventoryMap());
            infoMap.put("packages2", getPackages2Map());
            infoMap.put("cpuModel", "Intel(R) Xeon(R) CPU E5606 @ 2.13GHz");
            infoMap.put("hooks", map());
            infoMap.put("cpuSockets", "1");
            infoMap.put("vmTypes", getVmTypesList());
            infoMap.put("supportedProtocols", getSupportedProtocolsList());
            infoMap.put("networks", getNetworksMap(host));
            infoMap.put("bridges", getBridgesMap(host));
            infoMap.put("uuid", host.getUuid() + "_80:" + host.getMacAddress()); // 018CE76D-8EFE-D511-B30D-80C16E727330_80:c1:6e:6c:51:54
            infoMap.put("lastClientIface", getNetworkBridgeName());
            infoMap.put("nics", getNicsMap(host));
            infoMap.put("software_revision", "0.141");
            infoMap.put("clusterLevels", getClusterLevelsList());
            infoMap.put("cpuFlags", "fpu,vme,de,pse,tsc,msr,pae,mce,cx8,apic,sep,mtrr,pge,mca,cmov,pat,pse36,clflush,dts,acpi,mmx,fxsr,sse,sse2,ss,ht,tm,pbe,syscall,nx,pdpe1gb,rdtscp,lm,constant_tsc,arch_perfmon,pebs,bts,rep_good,nopl,xtopology,nonstop_tsc,aperfmperf,pni,pclmulqdq,dtes64,monitor,ds_cpl,vmx,smx,est,tm2,ssse3,cx16,xtpr,pdcm,pcid,dca,sse4_1,sse4_2,popcnt,aes,lahf_lm,arat,dtherm,tpr_shadow,vnmi,flexpriority,ept,vpid,model_Nehalem,model_Conroe,model_coreduo,model_core2duo,model_Penryn,model_Westmere,model_n270");
            infoMap.put("ISCSIInitiatorName", "iqn.1994-05.com.example:ef52ec17bb0");
            infoMap.put("netConfigDirty", "False");
            infoMap.put("supportedENGINEs", getSupportedENGINEsList());
            infoMap.put("reservedMem", "321");
            infoMap.put("bondings", getBondingsMap());
            infoMap.put("software_version", "4.10");
            infoMap.put("memSize", "7976");
            infoMap.put("cpuSpeed", "1200.000");
            infoMap.put("version_name", "Snow Man");
            infoMap.put("vlans", map());
            infoMap.put("cpuCores", "4");
            infoMap.put("kvmEnabled", "true");
            infoMap.put("guestOverhead", "65");
            infoMap.put("management_ip", ""); // null
            infoMap.put("cpuThreads", "4");
            infoMap.put("emulatedMachines", getEmulatedMachinesList());
            infoMap.put("operatingSystem", getOperatingSystemMap());
            infoMap.put("lastClient", "10.36.6.76");

            resultMap.put("info", infoMap);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }

    }

    private Map getHBAInventoryMap() {
        // HBAInventory
        Map initiatorMap = map();
        initiatorMap.put("InitiatorName", "iqn.1994-05.com.example:ef52ec17bb0");

        List iSCSIList = lst();
        iSCSIList.add(initiatorMap);

        Map iSCSIMap = map();
        iSCSIMap.put("iSCSI", iSCSIList);

        Map resultMap = map();
        resultMap.put("iSCSI", iSCSIList);
        resultMap.put("FC", lst());

        return resultMap;
    }

    private Map getPackages2Map() {
        Map resultMap = map();

        Map kernelMap = map();
        kernelMap.put("release", "5.fc17.x86_64");
        kernelMap.put("buildtime", "1357699251.0");
        kernelMap.put("version", "3.6.11");

        Map spiceServerMap = map();
        spiceServerMap.put("release", "5.fc17");
        spiceServerMap.put("buildtime", "1336983054");
        spiceServerMap.put("version", "0.10.1");

        Map vdsmMap = map();
        vdsmMap.put("release", "0.141.gita11e8f2.fc17");
        vdsmMap.put("buildtime", "1359653302");
        vdsmMap.put("version", "4.10.3");

        Map qemuKvmMap = map();
        qemuKvmMap.put("release", "2.fc17");
        qemuKvmMap.put("buildtime", "1349642820");
        qemuKvmMap.put("version", "1.0.1");

        Map libvirtMap = map();
        libvirtMap.put("release", "2.fc17");
        libvirtMap.put("buildtime", "1349642820");
        libvirtMap.put("version", "1.0.1");

        Map qemuImgMap = map();
        qemuImgMap.put("release", "2.fc17");
        qemuImgMap.put("buildtime", "1349642820");
        qemuImgMap.put("version", "1.0.1");

        Map momMap = map();
        momMap.put("release", "1.fc17");
        momMap.put("buildtime", "1354824066");
        momMap.put("version", "0.3.0");

        resultMap.put("kernel", kernelMap);
        resultMap.put("spice-server", spiceServerMap);
        resultMap.put("vdsm", vdsmMap);
        resultMap.put("qemu-kvm", qemuKvmMap);
        resultMap.put("libvirt", libvirtMap);
        resultMap.put("qemu-img", qemuImgMap);
        resultMap.put("mom", momMap);

        return resultMap;
    }

    List getVmTypesList() {
        List resultList = lst();
        resultList.add("kvm");
        return resultList;
    }

    List getSupportedProtocolsList() {
        List resultList = lst();
        resultList.add("2.2");
        resultList.add("2.3");
        return resultList;
    }

    Map getNetworksMap(Host host) {
        Map resultMap = map();

        Map ovirtmgmtMap = map();
        resultMap.put(getNetworkBridgeName(), ovirtmgmtMap);

        ovirtmgmtMap.put("iface", getNetworkBridgeName());
        ovirtmgmtMap.put("addr", host.getIpAddress()); // 10.34.63.177

        Map cfgMap = map();
        cfgMap.put("DEVICE", getNetworkBridgeName());
        cfgMap.put("DELAY", "0");
        cfgMap.put("BOOTPROTO", "dhcp");
        cfgMap.put("TYPE", "Ethernet");
        cfgMap.put("ONBOOT", "yes");

        ovirtmgmtMap.put("cfg", cfgMap);
        ovirtmgmtMap.put("mtu", "1500");
        ovirtmgmtMap.put("netmask", "255.255.252.0");
        ovirtmgmtMap.put("stp", "off");
        ovirtmgmtMap.put("bridged", Boolean.TRUE); // boolean..1
        ovirtmgmtMap.put("gateway", "10.34.63.254");

        List portsList = lst();
        portsList.add("em1");
        ovirtmgmtMap.put("ports", portsList);

        return resultMap;
    }

    Map getBridgesMap(Host host) {
        Map resultMap = map();

        Map ovirtmgmtMap = map();
        resultMap.put(getNetworkBridgeName(), ovirtmgmtMap);

        ovirtmgmtMap.put("addr", host.getIpAddress()); // 10.34.63.177
        ovirtmgmtMap.put("mtu", "1500");
        ovirtmgmtMap.put("netmask", "255.255.252.0");
        ovirtmgmtMap.put("stp", "off");
        ovirtmgmtMap.put("gateway", host.getIpAddress("GATEWAY")); // 10.34.63.254

        Map cfgMap = map();
        cfgMap.put("DEVICE", getNetworkBridgeName());
        cfgMap.put("DELAY", "0");
        cfgMap.put("BOOTPROTO", "dhcp");
        cfgMap.put("TYPE", "Ethernet");
        cfgMap.put("ONBOOT", "yes");

        ovirtmgmtMap.put("cfg", cfgMap);

        List portsList = lst();
        portsList.add("em1");
        ovirtmgmtMap.put("ports", portsList);

        return resultMap;
    }

    Map getNicsMap(Host host) {
        Map resultMap = map();

        Map em1Map = map();
        Map em2Map = map();

        resultMap.put("em1", em1Map);
        resultMap.put("em2", em2Map);

        Map cfg1Map = map();
        cfg1Map.put("BRIDGE", getNetworkBridgeName());
        cfg1Map.put("DEVICE", "em1");
        cfg1Map.put("UUID", host.getUuid("EM1")); // 1c7b3a5a-500f-41ec-ae03-bb619aeb4081
        cfg1Map.put("NETBOOT", "yes");
        cfg1Map.put("NM_CONTROLLED", "yes");
        cfg1Map.put("BOOTPROTO", "dhcp");
        cfg1Map.put("HWADDR", host.getMacAddress("EM1")); // 80:c1:6e:6c:51:54
        cfg1Map.put("TYPE", "Ethernet");
        cfg1Map.put("ONBOOT", "yes");
        cfg1Map.put("NAME", "Boot Disk");

        em1Map.put("cfg", cfg1Map);
        em1Map.put("addr", ""); // null
        em1Map.put("mtu", "1500");
        em1Map.put("netmask", ""); // null
        em1Map.put("hwaddr", host.getMacAddress("EM1")); // 80:c1:6e:6c:51:54
        em1Map.put("speed", Integer.valueOf(1000));

        Map cfg2Map = map();
        cfg2Map.put("BRIDGE", getNetworkBridgeName());
        cfg2Map.put("DEVICE", "em2");
        cfg2Map.put("UUID", host.getUuid("EM2")); // 011c667a-5c74-4882-9b62-35da3021cf8
        cfg2Map.put("NETBOOT", "yes");
        cfg2Map.put("NM_CONTROLLED", "yes");
        cfg2Map.put("BOOTPROTO", "dhcp");
        cfg2Map.put("HWADDR", host.getMacAddress("EM2")); // 80:C1:6E:6C:51:55
        cfg2Map.put("TYPE", "Ethernet");
        cfg2Map.put("ONBOOT", "no");

        em2Map.put("cfg", cfg2Map);
        em2Map.put("addr", ""); // null
        em2Map.put("mtu", "1500");
        em2Map.put("netmask", ""); // null
        em2Map.put("hwaddr", host.getMacAddress("EM2")); // 80:C1:6E:6C:51:55
        em2Map.put("speed", Integer.valueOf(1000));

        return resultMap;

    }

    public List getClusterLevelsList() {
        List resultList = lst();
        resultList.add("3.0");
        resultList.add("3.1");
        resultList.add("3.2");
        resultList.add("3.3");

        return resultList;
    }

    public List getSupportedENGINEsList() {
        List resultList = lst();
        resultList.add("3.0");
        resultList.add("3.1");
        resultList.add("3.2");
        resultList.add("3.3");

        return resultList;
    }

    public Map getBondingsMap() {
        Map resultMap = map();

        for (int i=0;i < 5;i++) {
            Map bondMap = map();
            bondMap.put("addr", ""); // null
            bondMap.put("cfg", map());
            bondMap.put("mtu", "150");
            bondMap.put("netmask", ""); // null
            bondMap.put("slaves", lst());
            bondMap.put("hwaddr", "00:00:00:00:00:00");

            resultMap.put("bond" + i, bondMap);
        }

        return resultMap;
    }

    public List getEmulatedMachinesList() {
        List resultList = lst();
        resultList.add("pc-0.10");
        resultList.add("pc-0.11");
        resultList.add("pc-0.12");
        resultList.add("pc-0.13");
        resultList.add("pc-0.14");
        resultList.add("pc-0.15");
        resultList.add("pc-1.0");
        resultList.add("pc-1.0");
        resultList.add("rhel6.4.0");
        resultList.add("pc");
        resultList.add("isapc");

        return resultList;
    }

    public Map getOperatingSystemMap() {
        Map resultMap = map();
        resultMap.put("release", "1");
        resultMap.put("version", "17");
        resultMap.put("name", "Fedora");

        return resultMap;
    }

    public Map getVdsHardwareInfo() {
        final Host host = getActiveHost();

        Map resultMap = getDoneStatus();

        Map infoMap = map();
        infoMap.put("systemProductName", "ProLiant DL160 G6");
        infoMap.put("systemSerialNumber", "CZJ2320M6N");
        infoMap.put("systemFamily", ""); // null
        infoMap.put("systemVersion", ""); // null
        infoMap.put("systemUUID", host.getUuid()); // 018CE76D-8EFE-D511-B30D-80C16E727330
        infoMap.put("systemManufacturer", "HP");

        resultMap.put("info", infoMap);

        return resultMap;
    }

    public Map getVdsStats() {
        final Host host = getActiveHost();

        try {
            Map resultMap = getDoneStatus();

            Map infoMap = map();
            infoMap.put("memShared", Integer.valueOf(0));
            infoMap.put("thpState", "always");
            infoMap.put("netConfigDirty", "False");
            infoMap.put("rxRate", "0.00");

            int nTotal = 0;
            for (VM vm : getActiveHost().getRunningVMs().values()) {
                if (!vm.isForDelete()) {
                    nTotal++;
                }
            }

            infoMap.put("vmCount", nTotal);
            infoMap.put("memUsed", getRandomNum(2));
            infoMap.put("storageDomains", getStorageDomainsStatsMap());
            infoMap.put("network", getNetworkStatMap());
            infoMap.put("txDropped", "0");
            infoMap.put("cpuUser", "0." + getRandomNum(2));
            infoMap.put("ksmPages", Integer.valueOf(100));
            infoMap.put("elapsedTime", host.getElapsedTimeInSeconds() + "");
            infoMap.put("cpuLoad", "0." + getRandomNum(2));
            infoMap.put("cpuSys", "0." + getRandomNum(2));
            infoMap.put("diskStats", getDiskStatsMap());
            infoMap.put("memCommitted", Integer.valueOf(0));
            infoMap.put("ksmState", Boolean.FALSE); //boolean..0

            int nMigrating = 0;
            for (VM vm : host.getRunningVMs().values()) {
                if (vm.getStatus() == VM.VMStatus.MigratingFrom || vm.getStatus() == VM.VMStatus.MigratingTo) {
                    nMigrating++;
                }
            }

            infoMap.put("vmMigrating", nMigrating);
            infoMap.put("ksmCpu", Integer.valueOf(0));
            infoMap.put("memAvailable", Integer.valueOf(6435));
            infoMap.put("txRate", "");
            infoMap.put("cpuUserVdsmd", "0.50");
            infoMap.put("momStatus", "active");
            infoMap.put("generationID", host.getUuid("GENERATION_ID")); // 28f88125-6e5e-4804-8c5d-b4620f80bc9c
            infoMap.put("rxDropped", "14965");
            infoMap.put("swapTotal", Integer.valueOf(20031));
            infoMap.put("swapFree", Integer.valueOf(20031));
            infoMap.put("statsAge", "0.43");
            infoMap.put("dateTime", host.getDateTimeGMT()); // 2013-02-10T19:09:11 GMT
            infoMap.put("anonHugePages", "662");
            infoMap.put("cpuIdle", "99.66");

            int nActive = 0;
            for (VM vm : getActiveHost().getRunningVMs().values()) {
                if (vm.getStatus() == VM.VMStatus.Up) {
                    nActive++;
                }
            }

            infoMap.put("vmActive", nActive);
            infoMap.put("cpuSysVdsmd", "0.25");

            resultMap.put("info", infoMap);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    Map getStorageDomainsStatsMap() {
        final Host host = getActiveHost();

        Map resultMap = map();

        if(host.getSpUUID() != null) {
            final DataCenter dataCenter = getDataCenterById(host.getSpUUID());

            for(StorageDomain storageDomain : dataCenter.getStorageDomainMap().values()) {
                // for all domains
                Map domainMap = map();
                domainMap.put("delay", "0.0141088962555");
                domainMap.put("lastCheck", "8.8");
                domainMap.put("code", Integer.valueOf(0));
                domainMap.put("valid", Boolean.TRUE);

                resultMap.put(storageDomain.getId(), domainMap); // ac3f03a9-ec72-49bc-8afc-60630ae63e88
            }
        }

        return resultMap;
    }

    Map getNetworkStatMap() {
        Map resultMap = map();

        String[] nets = new String[] { "bond0", "bond1", "bond2", "bond3", "bond3", "bond4", "em1", "em2" };
        for(String netName : nets) {

            Map netStats = map();
            netStats.put("txErrors", "0");
            netStats.put("state", "up");
            netStats.put("macAddr", ""); // null
            netStats.put("name", netName);
            netStats.put("txDropped", "0");
            netStats.put("txRate", "0.0");
            netStats.put("rxErrors", "0");
            netStats.put("rxRate", "0.0");
            netStats.put("rxDropped", "14965");

            resultMap.put(netName, netStats);
        }

        return resultMap;
    }

    Map getDiskStatsMap() {
        Map resultMap = map();

        Map freeMap = map();
        freeMap.put("free", "44231");
        resultMap.put("/var/log", freeMap);

        freeMap = map();
        freeMap.put("free", "44231");
        resultMap.put("/var/log/core", freeMap);

        freeMap = map();
        freeMap.put("free", "3978");
        resultMap.put("/var/run/vdsm/", freeMap);

        freeMap = map();
        freeMap.put("free", "44231");
        resultMap.put("/tmp", freeMap);

        return resultMap;
    }

    private String getNetworkBridgeName() {
        return AppConfig.getInstance().getNetworkBridgeName();
    }

}
