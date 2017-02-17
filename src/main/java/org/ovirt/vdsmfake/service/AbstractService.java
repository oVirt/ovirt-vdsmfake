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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ovirt.vdsmfake.ContextHolder;
import org.ovirt.vdsmfake.Utils;
import org.ovirt.vdsmfake.domain.DataCenter;
import org.ovirt.vdsmfake.domain.Host;
import org.ovirt.vdsmfake.domain.StorageDomain;
import org.ovirt.vdsmfake.domain.Task;
import org.ovirt.vdsmfake.domain.VdsmManager;
import org.ovirt.vdsmfake.task.TaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractService{

    public long constantDelay;
    public long randomDelay;
    protected Logger log = LoggerFactory.getLogger(AbstractService.class);


    public static Map getStatusMap(String message, int code) {

        Map<String, Object> resultMap = new HashMap();
        Map<String, Object> statusMap = new HashMap();

        statusMap.put("message", message);
        statusMap.put("code", Integer.valueOf(code));

        resultMap.put("status", statusMap);

        return resultMap;
    }

    public Map getOKStatus() {
        return getStatusMap("OK", 0);
    }

    public Map getOKStatusNotImplemented() {
        log.warn("The method is not fully implemented!", new Exception());
        return getStatusMap("OK", 0);
    }

    public Map<String, Integer> getDoneStatus() {
        return getStatusMap("Done", 0);
    }

    public Map map() {
        return new HashMap();
    }

    public List lst() {
        return new ArrayList();
    }

    public Host getActiveHost() {
        final String serverName = ContextHolder.getServerName();
        final Host host = VdsmManager.getInstance().getHostByName(serverName);
        return host;
    }

    public Host getActiveHostByName(String serverName) {
        return VdsmManager.getInstance().getHostByName(serverName);
    }

    public void updateHost(Host host) {
        VdsmManager.getInstance().updateHost(host);
    }

    public DataCenter getDataCenterById(String id) {
        // bind a copy of data center to the host
        final DataCenter dataCenter = VdsmManager.getInstance().getDataCenterById(id);
        return dataCenter;
    }

    public void updateDataCenter(DataCenter dataCenter) {
        VdsmManager.getInstance().updateDataCenter(dataCenter);
    }

    public Host getHostByName(String name) {
        return VdsmManager.getInstance().getHostByName(name);
    }

    public StorageDomain getStorageDomainById(String id) {
        return VdsmManager.getInstance().getStorageDomainById(id);
    }

    public void removeStorageDomain(StorageDomain storageDomain) {
        VdsmManager.getInstance().removeStorageDomain(storageDomain);
    }

    public void updateStorageDomain(StorageDomain storageDomain) {
        VdsmManager.getInstance().updateStorageDomain(storageDomain);
    }

    public void setMasterDomain(String spUuid, String masterSdUuid) {
        VdsmManager.getInstance().setMasterDomain(spUuid, masterSdUuid);
    }

    public RuntimeException error(Throwable t) {
        String message = t.getMessage() == null ? t.getClass().getName() : t.getMessage();
        log.error(message, t);
        return new RuntimeException(message, t);
    }

    public String getUuid() {
        return Utils.getUuid();
    }

    public String getRandomNum(int length) {
        return Utils.getRandomNum(length);
    }

    public void syncTask(Host host, Task task){
        if (host == null){
            host = getActiveHost();
            log.debug("host is null, task {} will be sync by any active host {}", task.getName(), host.getName());
        }

        host.getRunningTasks().put(task.getId(), task);
        log.debug("sync task:{} to host:{}", task.getName(), host.getName());
        TaskProcessor.getInstance().setTasksMap(host.getName(), task.getId());
    }
}
