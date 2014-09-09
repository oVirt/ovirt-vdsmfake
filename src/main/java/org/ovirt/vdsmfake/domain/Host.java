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

import java.util.Hashtable;
import java.util.Map;

import org.ovirt.vdsmfake.Utils;

/**
 *
 *
 */
public class Host extends BaseObject {
    /**
     *
     */
    private static final long serialVersionUID = -5570247376407615153L;

    String spUUID;
    long timeCreated;

    public enum SpmStatus {
        ACQUIRED("SPM"),
        CONTEND("Contend"),
        FREE("Free");

        String name;
        private SpmStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    int spmId = -1;
    SpmStatus spmStatus = SpmStatus.FREE;
    int spmLver = -1;

    final Map<String, VM> runningVMsMap = new Hashtable<String, VM>();
    final Map<String, String> propsMap = new Hashtable<String, String>();
    final Map<String, Task> runningTasks = new Hashtable<String, Task>();

    public Map<String, VM> getRunningVMs() {
        return runningVMsMap;
    }

    public Map<String, String> getPropsMap() {
        return propsMap;
    }

    public Map<String, Task> getRunningTasks() {
        return runningTasks;
    }

    public String getSpUUID() {
        return spUUID;
    }

    public void setSpUUID(String spUUID) {
        this.spUUID = spUUID;
    }

    public int getSpmId() {
        return spmId;
    }

    public void setSpmId(int spmId) {
        this.spmId = spmId;
    }

    public SpmStatus getSpmStatus() {
        return spmStatus == null ? SpmStatus.FREE : spmStatus;
    }

    public void setSpmStatus(SpmStatus spmStatus) {
        this.spmStatus = spmStatus;
    }

    public int getSpmLver() {
        return spmLver;
    }

    public void setSpmLver(int spmLver) {
        this.spmLver = spmLver;
    }

    public String getUuid() {
        return getUuid(null);
    }

    public String getUuid(String key) {
        return propsMap.get(key == null ? "UUID" : "UUID_" + key);
    }

    public String getIpAddress() {
        return getIpAddress(null);
    }

    public String getIpAddress(String key) {
        return propsMap.get(key == null ? "IP" : "IP_" + key);
    }

    public String getMacAddress() {
        return getMacAddress(null);
    }

    public String getMacAddress(String key) {
        return propsMap.get(key == null ? "MAC" : "MAC_" + key);
    }

    public String getDateTimeGMT() {
        return Utils.getDateTimeGMT();
    }

    public void initializeHost() {
        propsMap.put("UUID", Utils.getUuid());
        propsMap.put("UUID_EM1", Utils.getUuid());
        propsMap.put("UUID_EM2", Utils.getUuid());
        propsMap.put("UUID_GENERATION_ID", Utils.getUuid());

        propsMap.put("IP", Utils.ipGenerator());
        propsMap.put("IP_GATEWAY", Utils.ipGenerator());

        propsMap.put("MAC", Utils.getMacAddress());
        propsMap.put("MAC_EM1", Utils.getMacAddress());
        propsMap.put("MAC_EM2", Utils.getMacAddress());
    }

    public String getElapsedTimeInSeconds() {
        return "" + ((long) ((System.currentTimeMillis() - timeCreated) / 1000.0));
    }

}
