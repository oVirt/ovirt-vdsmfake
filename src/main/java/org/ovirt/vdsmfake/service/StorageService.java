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
import java.util.List;
import java.util.Map;

import org.ovirt.vdsmfake.Utils;
import org.ovirt.vdsmfake.domain.DataCenter;
import org.ovirt.vdsmfake.domain.Host;
import org.ovirt.vdsmfake.domain.StorageDomain;
import org.ovirt.vdsmfake.domain.StorageDomain.StorageType;
import org.ovirt.vdsmfake.domain.Task;
import org.ovirt.vdsmfake.domain.Volume;
import org.ovirt.vdsmfake.task.TaskProcessor;
import org.ovirt.vdsmfake.task.TaskRequest;
import org.ovirt.vdsmfake.task.TaskType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StorageService extends AbstractService {

    final static StorageService instance = new StorageService();

    public static StorageService getInstance() {
        return instance;
    }

    public StorageService() {
    }

    /**
     * Connect data center to the host.
     *
     * @param spUUID
     * @param hostID
     * @param scsiKey
     * @param msdUUID
     * @param masterVersion
     * @return
     */
    public Map connectStoragePool(String spUUID, Integer hostID, String scsiKey, String msdUUID, Integer masterVersion) {
        final DataCenter dataCenter = getDataCenterById(spUUID);

        // save to model
        final Host host = getActiveHost();
        host.setSpUUID(spUUID);
        updateHost(host);

        dataCenter.setMasterStorageDomainId(msdUUID);
        dataCenter.setMasterVersion(masterVersion);

        final StorageDomain storageDomain = getStorageDomainById(msdUUID);
        dataCenter.getStorageDomainMap().put(storageDomain.getId(), storageDomain);

        // store to database
        setMasterDomain(spUUID, msdUUID);

        log.info("Data center {} connected.", spUUID);

        // send ok
        return getOKStatus();
    }

    public Map connectStorageServer(Integer domType, String spUUID, List<Map> storageDomains) {
        try {
            final DataCenter dataCenter = getDataCenterById(spUUID);
            final Host host = getActiveHost();

            host.setSpUUID(spUUID);
            // save to model
            updateHost(host);
            dataCenter.setStorageType(StorageType.getByCode(domType));
            updateDataCenter(dataCenter);

            Map resultMap = getOKStatus();

            List statusList = new ArrayList();
            resultMap.put("statuslist", statusList);

            // extract
            for(int i=0; storageDomains != null && i < storageDomains.size();i++) {
                final Map storageDomainMap = storageDomains.get(i);

                final String id = (String) storageDomainMap.get("id");
//                String port = (String) storageDomainMap.get("port");
                final String connection = (String) storageDomainMap.get("connection");
//                String portal = (String) storageDomainMap.get("portal");
//                String iqn = (String) storageDomainMap.get("iqn");
//                String password = (String) storageDomainMap.get("password");
//                String user = (String) storageDomainMap.get("user");

                log.info("Adding storage domain, spUUID: {} id: {}, connection: {}", new Object[] { spUUID, id, connection });

                final StorageDomain storageDomain = getStorageDomainById(id);
                storageDomain.setConnection(connection);
                dataCenter.getStorageDomainMap().put(id, storageDomain);

                // response
                final Map storageStatusMap = map();
                storageStatusMap.put("status", Integer.valueOf(0));
                storageStatusMap.put("id", id);
                statusList.add(storageStatusMap);
            }

            log.info("Storage server {} connected.", spUUID);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map validateStorageServerConnection(Integer domType, String spUUID, List<Map> storageDomains) {
        try {
            final DataCenter dataCenter = getDataCenterById(spUUID);

            // reply with data from the request
            final Map resultMap = getOKStatus();

            final List statusList = new ArrayList();
            resultMap.put("statusList", statusList);

            // extract
            for(int i=0;i < storageDomains.size();i++) {
                final Map storageDomainMap = storageDomains.get(i);

                final String id = (String) storageDomainMap.get("id");
//                String port = (String) storageDomainMap.get("port");
                final String connection = (String) storageDomainMap.get("connection");
//                String portal = (String) storageDomainMap.get("portal");
//                String iqn = (String) storageDomainMap.get("iqn");
//                String password = (String) storageDomainMap.get("password");
//                String user = (String) storageDomainMap.get("user");

                final StorageDomain storageDomain = getStorageDomainById(id);
                storageDomain.setConnection(connection);
                dataCenter.getStorageDomainMap().put(id, storageDomain);

                // response
                final Map storageStatusMap = map();
                storageStatusMap.put("status", Integer.valueOf(0));
                storageStatusMap.put("id", id);
                statusList.add(storageStatusMap);
            }

            log.info("Storage server {} validated.", spUUID);

            return resultMap;

        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map createStoragePool(int poolType, String spUUID, String poolName, String masterDom, List domList,
            int masterVersion, String lockPolicy, int lockRenewalIntervalSec, int leaseTimeSec, int ioOpTimeoutSec,
            int leaseRetries) {
        try {
            final DataCenter dataCenter = getDataCenterById(spUUID);

            // save to model
            final Host host = getActiveHost();
            host.setSpUUID(spUUID);
            updateHost(host);

            dataCenter.setId(spUUID);
            dataCenter.setName(poolName);
            dataCenter.setMasterStorageDomainId(masterDom);
            dataCenter.setMasterVersion(masterVersion);

            final StorageDomain storageDomain = getStorageDomainById(masterDom);
            dataCenter.getStorageDomainMap().put(storageDomain.getId(), storageDomain);

            // store to database
            setMasterDomain(spUUID, masterDom);

            log.info("Storage pool {} created, master domain: {}, total domains: {}",
                    new Object[] { spUUID, dataCenter.getMasterStorageDomainId(), dataCenter.getStorageDomainMap().size() });

            // send ok
            return getOKStatus();
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map createStorageDomain(Integer storageType, String sdUUID, String domainName, String typeSpecificArg, Integer domClass, String storageFormatType) {
        log.info("Storage domain sdUUID: {}, name: {} created.", sdUUID, domainName);

        StorageDomain storageDomain = getStorageDomainById(sdUUID);
        storageDomain.setName(domainName);
        storageDomain.setConnection(typeSpecificArg);
        storageDomain.setDomainClass(StorageDomain.DomainClass.getByCode(domClass));
        storageDomain.setStorageType(StorageDomain.StorageType.getByCode(storageType));
        // store
        updateStorageDomain(storageDomain);

        // send ok
        return getOKStatus();
    }

    public Map disconnectStorageServer(Integer domType, String spUUID, List<Map> storageDomains) {
        Map resultMap = getOKStatus();

        List statusList = new ArrayList();
        resultMap.put("statuslist", statusList);

        // extract
        for(int i=0;i < storageDomains.size();i++) {
            Map storageDomainMap = storageDomains.get(i);

            String id = (String) storageDomainMap.get("id");
//            String port = (String) storageDomainMap.get("port");
//            String connection = (String) storageDomainMap.get("connection");
//            String portal = (String) storageDomainMap.get("portal");
//            String iqn = (String) storageDomainMap.get("iqn");
//            String password = (String) storageDomainMap.get("password");
//            String user = (String) storageDomainMap.get("user");


            // response
            Map storageStatusMap = map();
            storageStatusMap.put("status", Integer.valueOf(0));
            storageStatusMap.put("id", id);
            statusList.add(storageStatusMap);
        }

        return resultMap;
    }

    public Map getStoragePoolInfo(String spUUID) {
        try {
            final DataCenter dataCenter = getDataCenterById(spUUID);
            final Host host = getActiveHost();

            Map resultMap = getOKStatus();

            Map infoMap = map();
            infoMap.put("spm_id", host.getSpmId());
            infoMap.put("master_uuid", dataCenter.getMasterStorageDomainId()); // 553c2cb4-54d1-4c30-b2c2-6cb41a03518d
            infoMap.put("name", dataCenter.getName());
            infoMap.put("version", "3");

            String isoDomainId = null;
//            StorageDomain masterStorageDomain = null;

            int i=0;
            StringBuilder b = new StringBuilder();
            for (StorageDomain storageDomain : dataCenter.getStorageDomainMap().values()) {
                b.append(storageDomain.getId()).append(":").append(storageDomain.getDomainStatus().getName());

                if (i != dataCenter.getStorageDomainMap().values().size() - 1) {
                    b.append(",");
                }

                if (storageDomain.getDomainClass() == StorageDomain.DomainClass.ISO) {
                    isoDomainId = storageDomain.getId();
                }

//                if (dataCenter.getMasterStorageDomainId().equals(storageDomain.getId())) {
//                    masterStorageDomain = storageDomain;
//                }
            }

            infoMap.put("domains", b.toString());
            // 67070f56-027f-4ece-958d-e226639b622b:Active,4a6a6ed3-13f9-4481-8c03-8d217f6baef6:Active,553c2cb4-54d1-4c30-b2c2-6cb41a03518d:Active

            infoMap.put("pool_status", dataCenter.getPoolStatus()); // connected
            infoMap.put("isoprefix",
                    isoDomainId == null ? "" :
                    "/rhev/data-center/" + spUUID + "/" + isoDomainId + "/images/11111111-1111-1111-1111-111111111111");
            infoMap.put("type", dataCenter.getStorageType().toString()); // NFS
            infoMap.put("master_ver", dataCenter.getMasterVersion());
            infoMap.put("lver", host.getSpmLver()); //  Integer.valueOf(2)

            Map dominfo = map();
            for (StorageDomain storageDomain : dataCenter.getStorageDomainMap().values()) {
                Map dominfoChild = map();
                dominfo.put(storageDomain.getId(), dominfoChild); // 67070f56-027f-4ece-958d-e226639b622b

                dominfoChild.put("status", storageDomain.getDomainStatus().getName());
                dominfoChild.put("diskfree", "59586904064");
                dominfoChild.put("alerts", lst()); // empty list
                dominfoChild.put("disktotal", "274792972288");
            }

            resultMap.put("info", infoMap);
            resultMap.put("dominfo", dominfo);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map getStorageDomainStats(String sdUUID) {
        Map resultMap = getOKStatus();

        Map statsMap = map();
        statsMap.put("mdasize", Integer.valueOf(0));
        statsMap.put("mdathreshold", Boolean.TRUE);
        statsMap.put("mdavalid", Boolean.TRUE);
        statsMap.put("diskfree", "57503383552");
        statsMap.put("disktotal", "274792972288");
        statsMap.put("mdafree", Integer.valueOf(0));

        resultMap.put("stats", statsMap);

        return resultMap;
    }

    public Map activateStorageDomain(String sdUUID, String spUUID) {
        try {
            log.info("Activating storage domain, spUUID: {} sdUUID: {}", new Object[] { spUUID, sdUUID });

            final DataCenter dataCenter = getDataCenterById(spUUID);
            final StorageDomain storageDomain = dataCenter.getStorageDomainMap().get(sdUUID);
            if (storageDomain != null) {
                storageDomain.setDomainStatus(StorageDomain.DomainStatus.ACTIVE);
                updateStorageDomain(storageDomain);
            }

            return getOKStatus();
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map deactivateStorageDomain(String sdUUID, String spUUID, String msdUUID, int masterVersion) {
        try {
            log.info("Deactivating storage domain, spUUID: {} sdUUID: {}", new Object[] { spUUID, sdUUID });

            final DataCenter dataCenter = getDataCenterById(spUUID);
            final StorageDomain storageDomain = dataCenter.getStorageDomainMap().get(sdUUID);
            storageDomain.setDomainStatus(StorageDomain.DomainStatus.ATTACHED);
            updateStorageDomain(storageDomain);

            return getOKStatus();
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map attachStorageDomain(String sdUUID, String spUUID) {
        try {
            log.info("Attaching storage domain, spUUID: {} sdUUID: {}", new Object[] { spUUID, sdUUID });

            final DataCenter dataCenter = getDataCenterById(spUUID);
            final StorageDomain storageDomain = getStorageDomainById(sdUUID);
            storageDomain.setDomainStatus(StorageDomain.DomainStatus.ATTACHED);
            dataCenter.getStorageDomainMap().put(sdUUID, storageDomain);
            storageDomain.setDataCenter(dataCenter);
            updateDataCenter(dataCenter);

            return getOKStatus();
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map detachStorageDomain(String sdUUID, String spUUID, String msdUUID, int masterVersion) {
        try {
            log.info("Detaching storage domain, spUUID: {} sdUUID: {}", new Object[] { spUUID, sdUUID });

            final DataCenter dataCenter = getDataCenterById(spUUID);
            final StorageDomain storageDomain = dataCenter.getStorageDomainMap().get(sdUUID);
            storageDomain.setDomainStatus(StorageDomain.DomainStatus.UNATTACHED);
            dataCenter.getStorageDomainMap().remove(storageDomain.getId());
            storageDomain.setDataCenter(null);
            updateDataCenter(dataCenter);

            return getOKStatus();
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map refreshStoragePool(String spUUID, String msdUUID, Integer masterVersion) {
        try {
            log.info("Refreshing storage pool, spUUID: {} msdUUID: {}", new Object[] { spUUID, msdUUID });

            final DataCenter dataCenter = getDataCenterById(spUUID);
            dataCenter.setMasterStorageDomainId(msdUUID);
            dataCenter.setMasterVersion(masterVersion);

            final StorageDomain storageDomain = getStorageDomainById(msdUUID);
            dataCenter.getStorageDomainMap().put(storageDomain.getId(), storageDomain);

            // storage into db
            setMasterDomain(spUUID, msdUUID);

            return getOKStatus();
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map getFloppyList(String spUUID) {
        Map resultMap = getOKStatus();
        resultMap.put("isolist", lst());

        return resultMap;
    }

    public Map getIsoList(String spUUID) {
        Map resultMap = getOKStatus();

        List fileList = lst();
        fileList.add("Fedora-17-x86_64-DVD.iso");

        resultMap.put("isolist", fileList);

        return resultMap;
    }

    public Map getFileList(String spUUID) {
        Map resultMap = getOKStatus();

        resultMap.put("files", lst());

        return resultMap;
    }

    public Map spmStart(String spUUID, String prevID, String prevLVER, String recoveryMode, String scsiFencing) {
        final Host host = getActiveHost();

        Map resultMap = getOKStatus();

        Task task = new Task(getUuid());
        resultMap.put("uuid", task.getId());

        task.setTarget(host);
        getActiveHost().getRunningTasks().put(task.getId(), task);
        TaskProcessor.setTasksMap(host.getName(), task.getId());

        TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.FINISH_START_SPM, 10000l, task));

        return resultMap;
    }

    public Map spmStop(String spUUID) {
        final Host host = getActiveHost();

        host.setSpmId(-1);
        host.setSpmStatus(Host.SpmStatus.FREE);
        host.setSpmLver(-1);
        updateHost(host);

        Map resultMap = getOKStatus();

        return resultMap;
    }

    public Map getSpmStatus(String uuid) {
        final Host host = getActiveHost();

        Map resultMap = getOKStatus();

        Map infoMap = map();
        infoMap.put("spmId", host.getSpmId());  //1
        infoMap.put("spmStatus", host.getSpmStatus().getName()); // SPM
        infoMap.put("spmLver", host.getSpmLver()); // 0

        resultMap.put("spm_st", infoMap);

        return resultMap;
    }

    public Map createVolume(String sdUUID,
            String spUUID,
            String imgUUID,
            String size,
            Integer volFormat,
            Integer preallocate,
            Integer diskType,
            String volUUID,
            String desc,
            String srcImgUUID,
            String srcVolUUID) {
        try {
            DataCenter dataCenter = getDataCenterById(spUUID);
            StorageDomain storageDomain = dataCenter.getStorageDomainMap().get(sdUUID);

            final Volume volume = new Volume();
            volume.setId(volUUID);
            volume.setSize(size);
            volume.setVolFormat(volFormat);
            volume.setPreallocate(preallocate);
            volume.setDiskType(diskType);
            volume.setImgUUID(imgUUID);
            volume.setDesc(desc);
            volume.setSrcImgUUID(srcImgUUID);
            volume.setSrcVolUUID(srcVolUUID);

            log.info("Adding volume: {} for sp: {}, sd: {}", new Object[] { volUUID, spUUID, sdUUID });

            storageDomain.getVolumes().put(volUUID, volume);
            updateDataCenter(dataCenter);

            final Map resultMap = getOKStatus();
            final Task task = new Task(getUuid());

            resultMap.put("uuid", task.getId());

            syncTask(null, task);

            TaskProcessor.getInstance().addTask(new TaskRequest(TaskType.FINISH_CREATE_VOLUME, 5000l, task));

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map getVolumeInfo(String sdUUID, String spUUID, String imgGUID, String volUUID) {
        try {
            DataCenter dataCenter = getDataCenterById(spUUID);
            StorageDomain storageDomain = dataCenter.getStorageDomainMap().get(sdUUID);

            Volume volume = storageDomain.getVolumes().get(volUUID);

            Map resultMap = getOKStatus();

            String cTime =  Integer.valueOf((int) (System.currentTimeMillis() / 1000.0)).toString();

            Map infoMap = map();
            infoMap.put("status", "OK");
            infoMap.put("domain", sdUUID); // f71ab74c-c7ae-4cdd-931b-14fb3d062076
            infoMap.put("voltype", "LEAF");
            infoMap.put("description", volume.getDesc());
            infoMap.put("parent", volume.getSrcVolUUID());
            infoMap.put("format", "RAW");
            infoMap.put("image", imgGUID); // caa6d117-75f0-4d98-98e5-f024ce3fd907
            infoMap.put("ctime", cTime);
            infoMap.put("disktype", volume.getDiskType());
            infoMap.put("legality", "LEGAL");
            infoMap.put("mtime", cTime);
            infoMap.put("apparentsize", volume.getSize());
            infoMap.put("children", lst());
            infoMap.put("capacity", volume.getSize());
            infoMap.put("uuid", volUUID); // ae745379-b417-44cc-beb9-b5d6b9d704e9
            infoMap.put("truesize", "0");
            infoMap.put("type", "SPARSE");

            resultMap.put("info", infoMap);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map getStorageDomainInfo(String sdUUID) {
        try {
            final Host host = getActiveHost();

            StorageDomain storageDomain = getStorageDomainById(sdUUID);
            DataCenter dataCenter = storageDomain.getDataCenter();

            Map resultMap = getOKStatus();

            Map infoMap = map();
            infoMap.put("uuid", storageDomain.getId());
            infoMap.put("lver", host.getSpmLver());
            infoMap.put("version", "0");
            infoMap.put("role", storageDomain.getDomainRole().getName());
            infoMap.put("remotePath", storageDomain.getConnection()); //  10.34.63.202:/mnt/export/nfs/lv1/test/iso
            infoMap.put("spm_id", host.getSpmId());
            infoMap.put("type", storageDomain.getStorageType().toString()); // NFS
            infoMap.put("class", storageDomain.getDomainClass().getName()); // Iso
            infoMap.put("name", storageDomain.getName());
            List poolList = lst();

            if (dataCenter != null) {
                infoMap.put("master_ver", dataCenter.getMasterVersion());

                poolList.add(dataCenter.getId());
                poolList.add(getUuid());
                poolList.add(getUuid()); // TODO: not sure what is it for the value, the response has 3 values, the
                                         // first is spUuid
            }

            infoMap.put("pool", poolList);
            resultMap.put("info", infoMap);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }

    public Map getStorageDomainsList(String spUUID, int domainType, int poolType, String path) {
        try {
            // spUUID, domainClass, storageType, remotePath

            DataCenter dataCenter = getDataCenterById(spUUID);

            Map resultMap = getOKStatus();

            List domlist = lst();

            // apply filter
            for (StorageDomain storageDomain : dataCenter.getStorageDomainMap().values()) {
                if (spUUID != null && !storageDomain.getId().equals(spUUID)) {
                    continue;
                }

                if (domainType != 0 && StorageDomain.DomainClass.getByCode(domainType) != storageDomain.getDomainClass()) {
                    continue;
                }

                if (poolType != 0 && StorageDomain.StorageType.getByCode(poolType) != storageDomain.getStorageType()) {
                    continue;
                }

                if (path != null && !path.equals(storageDomain.getConnection())) {
                    continue;
                }

                domlist.add(storageDomain.getId());
            }

            resultMap.put("domlist", domlist);

            return resultMap;
        } catch (Exception e) {
            throw error(e);
        }
    }
}
