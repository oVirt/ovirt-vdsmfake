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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;
import org.ovirt.vdsmfake.rpc.json.JsonRpcServer;
import org.ovirt.vdsmfake.task.TaskProcessor;

/**
 * Init/release data when application starts/ends
 */
public class AppLifecycleListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Application destroyed.");

        final TaskProcessor taskProcessor = TaskProcessor.getInstance();
        taskProcessor.destroy();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Application initialized.");

        AppConfig.getInstance();

        // Make sure that the logDir system property is always set for log4j configuration
        System.setProperty("logDir", AppConfig.getInstance().getLogDir());

        //Initialize log4j
        PropertyConfigurator.configure(getClass().getResource("/log4j.xml"));

        final TaskProcessor taskProcessor = TaskProcessor.getInstance();
        taskProcessor.init();

        int jsonPort = AppConfig.getInstance().getJsonListenPort();
        boolean encrypted = AppConfig.getInstance().isJsonSecured();
        String hostName = AppConfig.getInstance().getJsonHost();
        JsonRpcServer.initMonitoring();
        JsonRpcServer server = new JsonRpcServer(hostName, jsonPort, encrypted);
        server.start();
    }
}
