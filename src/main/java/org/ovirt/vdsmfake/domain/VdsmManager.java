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

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.ovirt.vdsmfake.PersistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton instance of all environment objects accessible from all hosts.
 */
@Singleton
public class VdsmManager implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(VdsmManager.class);
    private static final long serialVersionUID = -1241089276242324962L;

    @Inject
    private PersistUtils persistUtils;

    final ConcurrentMap<String, DataCenter> storagePools = new ConcurrentHashMap<>();
    final ConcurrentMap<String, Host> hostMap = new ConcurrentHashMap<String, Host>(0);
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
        persistUtils.store(baseObject);
    }

    void removeObject(BaseObject baseObject) {
        persistUtils.remove(baseObject);
    }

    Object loadObject(Class<?> clazz, String id) {
        return persistUtils.load(clazz, id);
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
            log.info("Host restored from cache, name: {}", serverName);
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

    public int getRunningVmsCount() {
        return hostMap.values().stream()
                .map(host -> host.getRunningVMs().size())
                .reduce((x, y) -> x + y).orElse(0);
    }

    public int getHostCount() {
        return hostMap.size();
    }

    public Collection<Host> getAllhosts() {
        return hostMap.values();
    }

    public DataCenter getStoragePoolById(String spId) {
        DataCenter cached = (DataCenter) persistUtils.load(DataCenter.class, spId);
        DataCenter pool = storagePools.computeIfAbsent(spId, id -> cached == null ? new DataCenter() : cached);
        persistUtils.store(pool);
        return pool;
    }

    public Collection<DataCenter> getAllStoragePools() {
        return storagePools.values();

    }
}
