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
package org.ovirt.vdsmfake.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ovirt.vdsmfake.domain.Device.DeviceType;

/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class VM extends BaseObject {

    long timeCreated;

    /**
     *
     */
    private static final long serialVersionUID = 931258755382405882L;

    Host host;

    public enum VMStatus {
        Unassigned(-1),
        Down(0),
        Up(1),
        PoweringUp(2),
        PoweredDown(3),
        Paused(4),
        MigratingFrom(5),
        MigratingTo(6),
        Unknown(7),
        NotResponding(8),
        WaitForLaunch(9),
        RebootInProgress(10),
        SavingState(11),
        RestoringState(12),
        Suspended(13),
        ImageIllegal(14),
        ImageLocked(15),
        PoweringDown(16);

        int type;

        VMStatus(int type) {
            this.type = type;
        }
    }

    int memSize;
    String cpuType;

    VMStatus status = VMStatus.WaitForLaunch;
    // from create method - raw
    List deviceList = new ArrayList();
    Map customMap = new HashMap();

    final List<Device> devices = new ArrayList<Device>();

    // after the migration is done, set this VM to be deleted not to be in statistics
    boolean forDelete = false;

    public VMStatus getStatus() {
        return status;
    }

    public void setStatus(VMStatus status) {
        this.status = status;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public List getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List deviceList) {
        this.deviceList = deviceList;
    }

    public Map getCustomMap() {
        return customMap;
    }

    public void setCustomMap(Map customMap) {
        this.customMap = customMap;
    }

    @Override
    public VM clone() {
        VM vm = new VM();
        vm.setId(id);
        vm.setName(name);
        vm.setHost(host);
        vm.setStatus(status);
        vm.setDeviceList(deviceList);
        vm.setMemSize(memSize);
        vm.setCpuType(cpuType);
        vm.setCustomMap(customMap);
        vm.getDevices().addAll(getDevices());

        return vm;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }

    public String getCpuType() {
        return cpuType;
    }

    public void setCpuType(String cpuType) {
        this.cpuType = cpuType;
    }

    public List<Device> getDevices() {
        return devices;
    }

    // qemu - generate address for device
    public void generateDevicesAddressIfMissing() {
        if (deviceList == null) {
            return;
        }

        final Set<String> usedSlots = new HashSet<String>();

        // Iteration 1
        for (Object o : deviceList) {
            final Map deviceMap = (Map) o;

            final Map addressMap = (Map) deviceMap.get("address");
            if (addressMap == null) {
                continue;
            }

//            <name>address</name>
//            <value>
//                <struct>
//                    <member>
//                        <name> domain</name>
//                        <value>0x0000</value>
//                    </member>
//                    <member>
//                        <name>bus</name>
//                        <value>0x00</value>
//                    </member>
//                    <member>
//                        <name> type</name>
//                        <value>pci</value>
//                    </member>
//                    <member>
//                        <name> function</name>
//                        <value>0x0</value>
//                    </member>
//                    <member>
//                        <name> slot</name>
//                        <value>0x02</value>
//                    </member>
//                </struct>
//            </value>

            usedSlots.add((String) addressMap.get(" slot"));
        }

        // Iteration 2
        for (Object o : deviceList) {
            final Map deviceMap = (Map) o;
            final Map addressMap = (Map) deviceMap.get("address");
            if (addressMap != null) {
                continue;
            }

            final Map addressMap2 = new HashMap();

            final String slot = getNextAvailableSlot(usedSlots);
            final Map slotMap = new HashMap();
            // why not trimmed?
            slotMap.put(" domain", "0x0000");
            slotMap.put(" bus", "0x00");
            slotMap.put(" type", "pci");
            slotMap.put(" function", "0x0");
            slotMap.put(" slot", slot);

            deviceMap.put("address", addressMap2);
        }
    }

    private String getNextAvailableSlot(Set<String> usedSlots) {
        int counter = 0;
        while (true) {
            final String slot = "0x" + (counter < 10 ? "0" : "") + counter;
            if (usedSlots.contains(slot)) {
                counter++;
                continue;
            }

            usedSlots.add(slot);
            return slot;
        }
    }

    // fill devices list of Device with deviceList of Map objects
    public void parseDevices() {
        if (deviceList == null) {
            return;
        }

        for (Object o : deviceList) {
            Map deviceMap = (Map) o;

            Device device = new Device();
            devices.add(device);

            device.setId((String) deviceMap.get("deviceId"));
            device.setName((String) deviceMap.get("device"));
            device.setDeviceType(Device.DeviceType.getByName((String) deviceMap.get("type")));
            device.setMacAddr((String) deviceMap.get("macAddr"));
            device.setIface((String) deviceMap.get("iface"));
            device.setPath((String) deviceMap.get("path"));
            device.setVolumeID((String) deviceMap.get("volumeID"));
            device.setImageID((String) deviceMap.get("imageID"));
            device.setDomainID((String) deviceMap.get("domainID"));
            device.setPoolID((String) deviceMap.get("poolID"));
            device.setFormat((String) deviceMap.get("format"));
            device.setReadonly((String) deviceMap.get("readonly"));
            device.setNicModel((String) deviceMap.get("nicModel")); // pv
            device.setFilter((String) deviceMap.get("filter"));
            device.setNetwork((String) deviceMap.get("network")); // ovirtmgmt, rhevm
        }
    }

    public List<Device> getDevicesByType(DeviceType deviceType) {
        List<Device> l = new ArrayList<Device>();
        for (Device device : devices) {
            if (device.getDeviceType() == deviceType) {
                l.add(device);
            }
        }
        return l;
    }

    public String getDisplayType() {
        for (Device device : devices) {
            if (device.getDeviceType() == DeviceType.VIDEO) {
                return device.getName();
            }
        }
        return "qxl";
    }

    public String getMacAddress() {
        for (Device device : devices) {
            if (device.getDeviceType() == DeviceType.NIC) {
                return device.getMacAddr();
            }
        }
        return "?";
    }

    public String getImageId() {
        for (Device device : devices) {
            if (device.getDeviceType() == DeviceType.DISK) {
                return device.getImageID();
            }
        }
        return "?";
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getElapsedTimeInSeconds() {
        return "" + ((long) ((System.currentTimeMillis() - timeCreated) / 1000.0));
    }

    public boolean isForDelete() {
        return forDelete;
    }

    public void setForDelete(boolean forDelete) {
        this.forDelete = forDelete;
    }

}
