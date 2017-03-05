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

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.ovirt.vdsmfake.rpc.json.CommandExecutor;
import org.ovirt.vdsmfake.rpc.json.DefaultExecutor;
import org.ovirt.vdsmfake.rpc.json.Hystrix;
import org.ovirt.vdsmfake.rpc.json.JsonRpcServer;
import org.ovirt.vdsmfake.task.TaskProcessor;

/**
 * Init/release data when application starts/ends
 */
public class AppLifecycleListener implements ServletContextListener {

    @Inject
    private AppConfig appConfig;
    @Inject
    private Instance<JsonRpcServer> jsonRpcServerInstance;
    @Inject
    private Instance<CommandExecutor> commandExecutors;

    public static class DefaultLiteral extends AnnotationLiteral<Default> implements Default {
        public static final DefaultLiteral INSTANCE = new DefaultLiteral();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Application destroyed.");

        final TaskProcessor taskProcessor = TaskProcessor.getInstance();
        taskProcessor.destroy();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Application initialized.");

        final TaskProcessor taskProcessor = TaskProcessor.getInstance();
        taskProcessor.init();
        jsonRpcServerInstance.get().initMonitoring();
        jsonRpcServerInstance.get().start();
    }

    @Produces
    public CommandExecutor commandExecutorProducer(
            @DefaultExecutor CommandExecutor defaultExector,
            @Hystrix CommandExecutor hystrixExecutor) {
        if (System.getProperty("vdsmfake.commandExecutor", "Default")
                .equalsIgnoreCase("hystrix")) {
            return hystrixExecutor;
        } else {
            return defaultExector;
        }
    }
}
