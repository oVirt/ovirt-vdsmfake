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

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ovirt.vdsmfake.AppConfig;
import org.ovirt.vdsmfake.PersistUtils;
import org.ovirt.vdsmfake.domain.StorageDomain.DomainRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton instance of all environment objects accessible from all hosts.
 *
 *
 *
 */
public class VdsmManager implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(VdsmManager.class);

    /**
     *
     */
    private static final long serialVersionUID = -1241089276242324962L;

    private static final VdsmManager instance = new VdsmManager();

    public static VdsmManager getInstance() {
        return instance;
    }

    final Map<String, DataCenter> dataCenterMap = new Hashtable<String, DataCenter>();
    final Map<String, Host> hostMap = new Hashtable<String, Host>(0);
    final Map<String, StorageDomain> storageDomainMap = new HashMap<String, StorageDomain>();
    public final Vector allRunningVms = new Vector<String>(0);
    final ConcurrentMap<String, Host> spmMap = new ConcurrentHashMap<>();

    public ConcurrentMap getSpmMap() {
        return this.spmMap;
    }

    public void setSpmMap(String spId, Host host) {
        this.spmMap.put(spId, host);
    }

    public void removeSpmFromMap(String spId) {
        this.spmMap.remove(spId);
    }

    public Map<String, Host> getHostMap() {
        return this.hostMap;
    }

    public Host getSpmHost(String spUUID){
        return this.spmMap.get(spUUID);
    }

    void storeObject(BaseObject baseObject) {
        final File f =
                new File(AppConfig.getInstance().getCacheDir(), baseObject.getClass().getSimpleName() + "_"
                        + baseObject.getId());
        PersistUtils.store(baseObject, f);
    }

    void removeObject(BaseObject baseObject) {
        final File f =
                new File(AppConfig.getInstance().getCacheDir(), baseObject.getClass().getSimpleName() + "_"
                        + baseObject.getId());
        if (f.exists()) {
            f.delete();
        }
    }

    Object loadObject(Class<?> clazz, String id) {
        final File f = new File(AppConfig.getInstance().getCacheDir(), clazz.getSimpleName() + "_" + id);
        if (!f.exists()) {
            return null;
        }

        final BaseObject baseObject =
                (BaseObject) PersistUtils.load(new File(AppConfig.getInstance().getCacheDir(), clazz.getSimpleName() + "_"
                        + id));
        baseObject.setLastUpdate(f.lastModified());
        return baseObject;
    }

    public synchronized Host getHostByName(String serverName) {
        Host host = null;

        if (hostMap.containsKey(serverName)) {
            return hostMap.get(serverName);
        }

        host = (Host) loadObject(Host.class, serverName);
        if (host == null) {
            host = new Host();
            host.setName(serverName);
            host.setId(serverName);
            // generate IP, MAC, ...
            host.initializeHost();

            // save to the cache
            storeObject(host);
        } else {
            log.info("Host restored from file, name: {}", serverName);
        }

        hostMap.put(serverName, host);

        return host;
    }

    public synchronized void updateHost(Host host) {
        if (!hostMap.containsKey(host.getId())) {
            hostMap.put(host.getId(), host);
        }

        log.info("Storing host: {}", host.getName());

        // save to the cache
        storeObject(host);
    }

    public synchronized DataCenter getDataCenterById(String id) {
        DataCenter dataCenter = null;

        if (dataCenterMap.containsKey(id)) {
            return dataCenterMap.get(id);
        }

        dataCenter = (DataCenter) loadObject(DataCenter.class, id);
        if (dataCenter == null) {
            dataCenter = new DataCenter();
            dataCenter.setId(id);
        } else {
            log.info("Data center restored from file, id: {}", id);
        }

        dataCenterMap.put(id, dataCenter);

        return dataCenter;
    }

    public synchronized void updateDataCenter(DataCenter dataCenter) {
        if (!dataCenterMap.containsKey(dataCenter.getId())) {
            dataCenterMap.put(dataCenter.getId(), dataCenter);
        }

        // save to the cache
        storeObject(dataCenter);

        log.info("Data center {} stored", dataCenter.getId());
    }

    public synchronized StorageDomain getStorageDomainById(String id) {
        StorageDomain storageDomain = null;

        if (storageDomainMap.containsKey(id)) {
            return storageDomainMap.get(id);
        }

        storageDomain = (StorageDomain) loadObject(StorageDomain.class, id);
        if (storageDomain == null) {
            storageDomain = new StorageDomain();
            storageDomain.setId(id);
        } else {
            log.info("Data center restored from file, id: {}", id);
        }

        storageDomainMap.put(id, storageDomain);

        return storageDomain;
    }

    public synchronized void removeStorageDomain(StorageDomain storageDomain) {
        storageDomainMap.remove(storageDomain.getId());

        log.info("Removing storage domain: {}", storageDomain.getId());

        // remove from the cache
        removeObject(storageDomain);
    }

    public synchronized void updateStorageDomain(StorageDomain storageDomain) {
        log.info("Updating storage domain: {}", storageDomain.getId());

        if (!storageDomainMap.containsKey(storageDomain.getId())) {
            storageDomainMap.put(storageDomain.getId(), storageDomain);
        }

        // save to the cache
        storeObject(storageDomain);
    }

    public synchronized void setMasterDomain(String spUuid, String masterSdUuid) {
        log.info("Setting master domain, sp: {}, master sd: {}: ", spUuid, masterSdUuid);

        if (masterSdUuid == null) {
            return;
        }

        final DataCenter dataCenter = getDataCenterById(spUuid);

        for (StorageDomain storageDomain : dataCenter.getStorageDomainMap().values()) {
            if (masterSdUuid.equals(storageDomain.getId())) {
                storageDomain.setDomainRole(DomainRole.MASTER);
            } else {
                storageDomain.setDomainRole(DomainRole.REGULAR);
            }
        }

        updateDataCenter(dataCenter);
    }
}
