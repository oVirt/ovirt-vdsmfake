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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 */
public class StorageDomain extends BaseObject {
    /**
     *
     */
    private static final long serialVersionUID = 1911526590448636424L;

    String connection;

    // DATA_DOMAIN = 1
    // ISO_DOMAIN = 2
    // BACKUP_DOMAIN = 3
    public enum DomainClass {
        DATA(1, "Data"),
        ISO(2, "Iso"),
        BACKUP(3, "Backup"); // EXPORT

        int code;
        String name;

        private DomainClass(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static DomainClass getByCode(int code) {
            for (DomainClass st : values()) {
                if (st.getCode() == code) {
                    return st;
                }
            }

            return null;
        }
    }

    DomainClass domainClass = DomainClass.DATA;

    String domainVersion;

    // DOM_UNKNOWN_STATUS = 'Unknown'
    // DOM_ATTACHED_STATUS = 'Attached'
    // DOM_UNATTACHED_STATUS = 'Unattached'
    // DOM_ACTIVE_STATUS = 'Active'
    public enum DomainStatus {
        UNKNOWN("Unknown"),
        ATTACHED("Attached"),
        UNATTACHED("Unattached"),
        ACTIVE("Active");

        String name;

        private DomainStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static DomainStatus getByName(String name) {
            for (DomainStatus ds : values()) {
                if (ds.getName().equals(name)) {
                    return ds;
                }
            }

            return null;
        }
    }

    DomainStatus domainStatus = DomainStatus.UNATTACHED;

    // use prefix for type name:
    // UNKNOWN_DOMAIN = 0
    // NFS_DOMAIN = 1
    // FCP_DOMAIN = 2
    // ISCSI_DOMAIN = 3
    // LOCALFS_DOMAIN = 4
    // CIFS_DOMAIN = 5
    // POSIXFS_DOMAIN = 6
    // GLUSTERFS_DOMAIN = 7
    public enum StorageType {
        UNKNOWN(0),
        NFS(1),
        FCP(2),
        ISCSI(3),
        LOCALFS(4),
        CIFS(5),
        POSIXFS(6),
        GLUSTERFS(7);

        int code;

        private StorageType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static StorageType getByCode(int code) {
            for (StorageType st : values()) {
                if (st.getCode() == code) {
                    return st;
                }
            }

            return null;
        }
    }

    StorageType storageType = StorageType.NFS;

    public enum DomainRole {
        MASTER("Master"),
        REGULAR("Regular");

        String name;

        private DomainRole(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    DomainRole domainRole = DomainRole.REGULAR;

    // set if attached
    DataCenter dataCenter;

    final ConcurrentMap<String, Volume> volumes = new ConcurrentHashMap<>();

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public DomainClass getDomainClass() {
        return domainClass;
    }

    public void setDomainClass(DomainClass domainClass) {
        this.domainClass = domainClass;
    }

    public Map<String, Volume> getVolumes() {
        return volumes;
    }

    public String getDomainVersion() {
        return domainVersion;
    }

    public void setDomainVersion(String domainVersion) {
        this.domainVersion = domainVersion;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public DomainRole getDomainRole() {
        return domainRole;
    }

    public void setDomainRole(DomainRole domainRole) {
        this.domainRole = domainRole;
    }

    public DomainStatus getDomainStatus() {
        return domainStatus;
    }

    public void setDomainStatus(DomainStatus domainStatus) {
        this.domainStatus = domainStatus;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

}
