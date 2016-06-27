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
import java.util.*;

/**
 * App config data
 *
 *
 *
 */
public class AppConfig {

    private static final AppConfig instance = new AppConfig();

    long constantDelay;
    long randomDelay;
    long delayMinimum;
    ArrayList storageDelay;
    ArrayList networkLoad;
    ArrayList cpuLoadList;
    ArrayList memLoad;
    String networkBridgeName;
    String cacheDir;
    String logDir;
    String forwardVdsmServer;
    String vdsmPort;
    String vmConfAndStatsConstants;
    String vmConfAndStatsUpdateIntervals;
    String targetServerUrl; // not set in web.xml
    int jsonHandlersThreadsPool;
    ArchitectureType architectureType;

    final Set<String> notLoggedMethodSet = new HashSet<String>();

    public static AppConfig getInstance() {
        return instance;
    }

    public void init(Map<String, String> paramMap) {
        constantDelay = Utils.getLong(paramMap.get("constantDelay"));
        randomDelay = Utils.getLong(paramMap.get("randomDelay"));
        networkLoad = Utils.splitString(paramMap.get("networkLoad"));
        cpuLoadList = Utils.splitString(paramMap.get("cpuLoad"));
        memLoad = Utils.splitString(paramMap.get("memLoad"));
        storageDelay = Utils.splitString(paramMap.get("storageDelay"));
        networkBridgeName = paramMap.get("networkBridgeName");
        cacheDir = paramMap.get("cacheDir");
        // Each run will store its logs separately
        logDir = paramMap.get("logDir") + "/" + System.currentTimeMillis();
        forwardVdsmServer = paramMap.get("forwardVdsmServer");
        vdsmPort = paramMap.get("vdsmPort");
        vmConfAndStatsConstants = paramMap.get("vmConfAndStatsConstants");
        vmConfAndStatsUpdateIntervals = paramMap.get("vmConfAndStatsUpdateIntervals");
        jsonHandlersThreadsPool = Integer.valueOf(paramMap.get("jsonHandlersThreadsPool"));
        architectureType = ArchitectureType.valueOf(paramMap.get("architectureType").toUpperCase());


        final String notLoggedMethods = paramMap.get("notLoggedMethods");
        // ...

        if (notLoggedMethods != null && notLoggedMethods.trim().length() > 0) {
            final String[] methodNames = notLoggedMethods.split(",");
            for (String methodName : methodNames) {
                notLoggedMethodSet.add(methodName.trim());
            }
        }

        makeDir(cacheDir);
        makeDir(logDir);

        if (isProxyActive()) {
            targetServerUrl = getForwardVdsmServer() + ":" + getVdsmPort() + "/";
        }
    }

    public String getVmConfAndStatsUpdateIntervals() {
        return vmConfAndStatsUpdateIntervals;
    }

    public String getVmConfAndStatsConstants() {
        return vmConfAndStatsConstants;
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

    public long getDelayMinimum() {
        return this.delayMinimum;
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

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getForwardVdsmServer() {
        return forwardVdsmServer;
    }

    public void setForwardVdsmServer(String forwardVdsmServer) {
        this.forwardVdsmServer = forwardVdsmServer;
    }

    public String getVdsmPort() {
        return vdsmPort;
    }

    public void setVdsmPort(String vdsmPort) {
        this.vdsmPort = vdsmPort;
    }

    public boolean isMethodLoggingEnabled(String methodName) {
        if (methodName == null) {
            return false;
        }

        if (notLoggedMethodSet.isEmpty()) {
            return true;
        }

        if (notLoggedMethodSet.contains("*")) {
            return false;
        }

        return !notLoggedMethodSet.contains(methodName);
    }

    public boolean isProxyActive() {
        return getForwardVdsmServer() != null && getForwardVdsmServer().trim().length() > 0;
    }

    public boolean isLogDirSet() {
        return getLogDir() != null && getLogDir().trim().length() > 0;
    }

    public String getTargetServerUrl() {
        return targetServerUrl;
    }

    private void makeDir(String dir) {
        if (dir != null && dir.trim().length() > 0 && !new File(dir).exists()) {
            new File(dir).mkdirs();
        }
    }

    public ArrayList getStorageDelay() {
        return storageDelay;
    }

    public ArrayList getCpuLoadValues(){
        return cpuLoadList;
    }

    public ArrayList getNetworkLoadValues() {
        return networkLoad;
    }

    public ArrayList getMemLoadValues() {
        return memLoad;
    }

    public int getJsonHandlersThreadsPool() {
        return jsonHandlersThreadsPool;
    }

    public ArchitectureType getArchitectureType() {
        return architectureType;
    }

    public enum  ArchitectureType {
        X86_64("Intel(R) Xeon(R) CPU E5606 @ 2.13GHz","fpu,vme,de,pse,tsc,msr,pae,mce,cx8,apic,sep,mtrr,pge,mca,cmov,pat,pse36,clflush,mmx,fxsr,sse,sse2,ss,syscall,nx,pdpe1gb,rdtscp,lm,constant_tsc,rep_good,nopl,eagerfpu,pni,pclmulqdq,vmx,ssse3,fma,cx16,pcid,sse4_1,sse4_2,x2apic,movbe,popcnt,tsc_deadline_timer,aes,xsave,avx,f16c,rdrand,hypervisor,lahf_lm,abm,tpr_shadow,vnmi,flexpriority,ept,fsgsbase,bmi1,avx2,smep,bmi2,erms,invpcid,xsaveopt,model_Haswell-noTSX,model_Nehalem,model_Conroe,model_Penryn,model_Westmere,model_SandyBridge"),
        PPC64("IBM POWER8","powernv,model_POWER8");

        private String cpuModel;
        private String cpuFlags;

        private ArchitectureType(String cpuModel, String cpuFlags){
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
}
