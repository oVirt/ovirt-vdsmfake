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

import java.util.HashMap;
import java.util.Map;

import org.ovirt.vdsmfake.domain.StorageDomain.StorageType;

/**
 * Data center with attached storage domains
 *
 *
 *
 */
public class DataCenter extends BaseObject {
    /**
     *
     */
    private static final long serialVersionUID = -5241720601365197280L;

    final Map<String, StorageDomain> storageDomainMap = new HashMap<String, StorageDomain>();

    String masterStorageDomainId;
    Integer masterVersion = 0;
    String poolStatus = "connected";


    StorageType storageType = StorageType.NFS;

    public Map<String, StorageDomain> getStorageDomainMap() {
        return storageDomainMap;
    }

    public String getMasterStorageDomainId() {
        return masterStorageDomainId == null ? this.id : masterStorageDomainId;
    }

    public void setMasterStorageDomainId(String masterStorageDomainId) {
        this.masterStorageDomainId = masterStorageDomainId;
    }

    public Integer getMasterVersion() {
        return masterVersion;
    }

    public void setMasterVersion(Integer masterVersion) {
        this.masterVersion = masterVersion;
    }

    public String getPoolStatus() {
        return poolStatus;
    }

    public void setPoolStatus(String poolStatus) {
        this.poolStatus = poolStatus;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

}
