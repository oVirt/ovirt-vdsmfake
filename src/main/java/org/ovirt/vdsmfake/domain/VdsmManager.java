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

    final ConcurrentMap<String, DataCenter> dataCenterMap = new ConcurrentHashMap<String, DataCenter>();
    final ConcurrentMap<String, Host> hostMap = new ConcurrentHashMap<String, Host>(0);
    final ConcurrentMap<String, StorageDomain> storageDomainMap = new ConcurrentHashMap<String, StorageDomain>();
    final ConcurrentMap<String, Host> spmMap = new ConcurrentHashMap<String, Host>();

    public void setSpmMap(String spId, Host host) {
        this.spmMap.put(spId, host);
    }

    public void removeSpmFromMap(String spId) {
        this.spmMap.remove(spId);
    }

    public Host getSpmHost(String spUUID){
        return this.spmMap.get(spUUID);
    }

    //TODO: caching and stored cached files needs a rework, when the app started, and when stored the files

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

    public Host getHostByName(String serverName) {
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

    public void updateHost(Host host) {
        hostMap.put(host.getId(), host);

        log.info("Storing host: {}", host.getName());

        // save to the cache
        storeObject(host);
    }

    public DataCenter getDataCenterById(String id) {
        DataCenter dataCenter = null;

        if (dataCenterMap.containsKey(id)) {
            return dataCenterMap.get(id);
        }

        // read cache from stored file
        dataCenter = (DataCenter) loadObject(DataCenter.class, id);

        if (dataCenter == null) {
            dataCenter = reInitializeDataCenter(id);
        } else {
            dataCenterMap.put(id, dataCenter);
        }

        return dataCenter;
    }

    private DataCenter reInitializeDataCenter(String dcId) throws RuntimeException {
        log.warn("about to reinitialize dc");
        DataCenter dataCenter = new DataCenter();
        dataCenter.setId(dcId);
        updateDataCenter(dataCenter);
        return dataCenter;
    }

    public void updateDataCenter(DataCenter dataCenter) {
        // save to the cache
        try {
            storeObject(dataCenter);
        } catch (Exception e) {
            log.error("failed to store object {}", e.getStackTrace());
        }

        dataCenterMap.put(dataCenter.getId(), dataCenter);

        log.info("Data center {} restored", dataCenter.getId());
    }

    public StorageDomain getStorageDomainById(String id) {
        StorageDomain storageDomain = null;

        if (storageDomainMap.containsKey(id)) {
            return storageDomainMap.get(id);
        }

        // read cache from stored file
        storageDomain = (StorageDomain) loadObject(StorageDomain.class, id);

        if (storageDomain == null) {
            storageDomain = recoverStorageDomain(id);
        } else {
            storageDomainMap.put(id, storageDomain);
        }

        return storageDomain;
    }

    private StorageDomain recoverStorageDomain(String sdUUID) {
        log.warn("about to recover SD {}", sdUUID);
        StorageDomain storageDomain = new StorageDomain();
        storageDomain.setId(sdUUID);
        updateStorageDomain(storageDomain);
        return storageDomain;
    }

    public void removeStorageDomain(StorageDomain storageDomain) {
        storageDomainMap.remove(storageDomain.getId());

        log.info("Removing storage domain: {}", storageDomain.getId());

        // remove from the cache
        removeObject(storageDomain);
    }

    public void updateStorageDomain(StorageDomain storageDomain) {
        log.info("Updating storage domain: {}", storageDomain.getId());

        storageDomainMap.put(storageDomain.getId(), storageDomain);

        // save to the cache
        storeObject(storageDomain);
    }

    public void setMasterDomain(String spUuid, String masterSdUuid) {
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

    public int getRunningVmsCount() {
        return hostMap.values().stream()
                .map(host -> host.getRunningVMs().size())
                .reduce((x, y) -> x + y).orElse(0);
    }

    public int getHostCount() {
        return hostMap.size();
    }
}
