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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    String networkBridgeName;
    String cacheDir;
    String logDir;
    String forwardVdsmServer;
    String vdsmPort;
    String targetServerUrl; // not set in web.xml

    final Set<String> notLoggedMethodSet = new HashSet<String>();

    public static AppConfig getInstance() {
        return instance;
    }

    public void init(Map<String, String> paramMap) {
        constantDelay = getLong(paramMap.get("constantDelay"));
        randomDelay = getLong(paramMap.get("randomDelay"));
        networkBridgeName = paramMap.get("networkBridgeName");
        cacheDir = paramMap.get("cacheDir");
        logDir = paramMap.get("logDir");
        forwardVdsmServer = paramMap.get("forwardVdsmServer");
        vdsmPort = paramMap.get("vdsmPort");

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

    long getLong(String val) {
        if (val == null || val.trim().length() == 0) {
            return 0;
        }

        try {
            return Long.valueOf(val);
        } catch (Exception e) {
            return 0;
        }
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

}
