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


/**
 *
 *
 */
public class Device extends BaseObject {

    // id filled with 'deviceId'
    // name filled with 'device'

    /**
     *
     */
    private static final long serialVersionUID = 8669012341724331785L;

    public enum DeviceType {
        DISK("disk"),
        NIC("interface"),
        VIDEO("video"),
        SOUND("sound"),
        CONTROLLER("controller"),
        GENERAL("general"),
        BALLOON("balloon"),
        REDIR("redir"),
        WATCHDOG("watchdog"),
        CONSOLE("console"),
        SMARTCARD("smartcard");

        String name;

        private DeviceType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static DeviceType getByName(String name) {
            for (DeviceType deviceType : values()) {
                if (deviceType.getName().equals(name)) {
                    return deviceType;
                }
            }

            return null;
        }
    }

    DeviceType deviceType = DeviceType.GENERAL; // 'type'

    String macAddr;
    String iface;
    String path;
    String volumeID;
    String imageID;
    String domainID;
    String poolID;
    String format;
    String readonly;
    String nicModel;
    String filter;
    String network; // ovirtmgmt, rhevm

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getIface() {
        return iface;
    }

    public void setIface(String iface) {
        this.iface = iface;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVolumeID() {
        return volumeID;
    }

    public void setVolumeID(String volumeID) {
        this.volumeID = volumeID;
    }

    public String getImageID() {
        if( imageID == null || imageID.isEmpty() ) {
            return "00000000-0000-0000-0000-000000000000";
        }
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getDomainID() {
        return domainID;
    }

    public void setDomainID(String domainID) {
        this.domainID = domainID;
    }

    public String getPoolID() {
        return poolID;
    }

    public void setPoolID(String poolID) {
        this.poolID = poolID;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getReadonly() {
        return readonly;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public String getNicModel() {
        return nicModel;
    }

    public void setNicModel(String nicModel) {
        this.nicModel = nicModel;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

}
