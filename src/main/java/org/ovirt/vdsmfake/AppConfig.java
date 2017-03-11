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
package org.ovirt.vdsmfake;

import java.io.File;
import java.util.List;

import javax.enterprise.inject.Alternative;

@Alternative
public class AppConfig {

    private long constantDelay;
    private long randomDelay;
    private long delayMinimum;
    private List<String> storageDelay;
    private List<String> networkLoad;
    private List<String> cpuLoad;
    private List<String> memLoad;
    private String networkBridgeName;
    private String cacheDir;
    private String forwardVdsmServer;
    private String vmConfAndStatsConstants;
    private String vmConfAndStatsUpdateIntervals;
    private List<String> eventSupportedMethods;
    private String targetServerUrl;
    private String architectureType;
    private boolean jsonEvents;
    private int eventsThreadPoolSize;
    private int jsonThreadPoolSize;
    private String certspath;
    private List<String> notLoggedMethods;
    private int jsonListenPort;
    private boolean jsonSecured;
    private String jsonHost;
    private List<String> emulatedMachines;

    public AppConfig() {
        makeDir(cacheDir);
    }

    private void makeDir(String dir) {
        if (dir != null && dir.trim().length() > 0 && !new File(dir).exists()) {
            new File(dir).mkdirs();
        }
    }

    public List<String> getEmulatedMachines() {
        return emulatedMachines;
    }

    public void setEmulatedMachines(List<String> emulatedMachines) {
        this.emulatedMachines = emulatedMachines;
    }

    public enum ArchitectureType {
        X86_64(
                "Intel(R) Xeon(R) CPU E5606 @ 2.13GHz",
                "fpu,vme,de,pse,tsc,msr,pae,mce,cx8,apic,sep,mtrr,pge,mca,cmov,pat,pse36,clflush,mmx,fxsr,sse,"
                        + "sse2,ss,syscall,nx,pdpe1gb,rdtscp,lm,constant_tsc,rep_good,nopl,eagerfpu,pni,"
                        + "pclmulqdq,vmx,ssse3,fma,cx16,pcid,sse4_1,sse4_2,x2apic,movbe,popcnt,tsc_deadline_timer,"
                        + "aes,xsave,avx,f16c,rdrand,hypervisor,lahf_lm,abm,tpr_shadow,vnmi,flexpriority,ept"
                        + ",fsgsbase,bmi1,avx2,smep,bmi2,erms,invpcid,xsaveopt,model_Haswell-noTSX,model_Nehalem,"
                        + "model_Conroe,model_Penryn,model_Westmere,model_SandyBridge"),
        PPC64(
                "IBM POWER8", "powernv,model_POWER8");

        private String cpuModel;
        private String cpuFlags;

        ArchitectureType(String cpuModel, String cpuFlags) {
            this.cpuModel = cpuModel;
            this.cpuFlags = cpuFlags;
        }

        public String getCpuModel() {
            return cpuModel;
        }

        public String getCpuFlags() {
            return cpuFlags;
        }
    }

    public long getConstantDelay() {
        return constantDelay;
    }

    public void setConstantDelay(long constantDelay) {
        this.constantDelay = constantDelay;
    }

    public long getRandomDelay() {
        return randomDelay;
    }

    public void setRandomDelay(long randomDelay) {
        this.randomDelay = randomDelay;
    }

    public long getDelayMinimum() {
        return delayMinimum;
    }

    public void setDelayMinimum(long delayMinimum) {
        this.delayMinimum = delayMinimum;
    }

    public List<String> getStorageDelay() {
        return storageDelay;
    }

    public void setStorageDelay(List<String> storageDelay) {
        this.storageDelay = storageDelay;
    }

    public List<String> getNetworkLoad() {
        return networkLoad;
    }

    public void setNetworkLoad(List<String> networkLoad) {
        this.networkLoad = networkLoad;
    }

    public List<String> getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(List<String> cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public List<String> getMemLoad() {
        return memLoad;
    }

    public void setMemLoad(List<String> memLoad) {
        this.memLoad = memLoad;
    }

    public String getNetworkBridgeName() {
        return networkBridgeName;
    }

    public void setNetworkBridgeName(String networkBridgeName) {
        this.networkBridgeName = networkBridgeName;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public List<String> getEventSupportedMethods() {
        return eventSupportedMethods;
    }

    public void setEventSupportedMethods(List<String> eventSupportedMethods) {
        this.eventSupportedMethods = eventSupportedMethods;
    }

    public String getArchitectureType() {
        return architectureType;
    }

    public void setArchitectureType(String architectureType) {
        this.architectureType = architectureType;
    }

    public boolean isJsonEvents() {
        return jsonEvents;
    }

    public void setJsonEvents(boolean jsonEvents) {
        this.jsonEvents = jsonEvents;
    }

    public int getEventsThreadPoolSize() {
        return eventsThreadPoolSize;
    }

    public void setEventsThreadPoolSize(int eventsThreadPoolSize) {
        this.eventsThreadPoolSize = eventsThreadPoolSize;
    }

    public int getJsonThreadPoolSize() {
        return jsonThreadPoolSize;
    }

    public void setJsonThreadPoolSize(int jsonThreadPoolSize) {
        this.jsonThreadPoolSize = jsonThreadPoolSize;
    }

    public String getCertspath() {
        return certspath;
    }

    public void setCertspath(String certspath) {
        this.certspath = certspath;
    }

    public List<String> getNotLoggedMethods() {
        return notLoggedMethods;
    }

    public void setNotLoggedMethods(List<String> notLoggedMethods) {
        this.notLoggedMethods = notLoggedMethods;
    }

    public int getJsonListenPort() {
        return jsonListenPort;
    }

    public void setJsonListenPort(int jsonListenPort) {
        this.jsonListenPort = jsonListenPort;
    }

    public boolean isJsonSecured() {
        return jsonSecured;
    }

    public void setJsonSecured(boolean jsonSecured) {
        this.jsonSecured = jsonSecured;
    }

    public String getJsonHost() {
        return jsonHost;
    }

    public void setJsonHost(String jsonHost) {
        this.jsonHost = jsonHost;
    }
}
