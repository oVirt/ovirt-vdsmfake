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
package org.ovirt.vdsmfake.task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class TaskProcessor extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(TaskProcessor.class);

    final static TaskProcessor instance = new TaskProcessor();
    final List<TaskRequest> queue = new LinkedList<TaskRequest>();
    private static ConcurrentMap<String, String> tasksmap = new ConcurrentHashMap<String, String>();
    Timer timer;

    private TaskProcessor() {
    }

    public static Map<String, String> getTasksMap() {
        return tasksmap;
    }

    public static void setTasksMap(String hostName, String taskId) {
        tasksmap.put(hostName, taskId);
    }

    public static void clearTaskMap(){
        tasksmap.clear();
    }

    public synchronized void addTask(TaskRequest taskRequest) {
        queue.add(taskRequest);
    }

    public static TaskProcessor getInstance() {
        return instance;
    }

    public void init() {
        timer = new Timer();
        timer.scheduleAtFixedRate(this, 0, 2000l); // every 2 seconds
    }

    public void destroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void run() {
        if (queue.isEmpty()) {
            return;
        }

        processAll();
    }

    private synchronized void processAll() {
        if (queue.isEmpty()) {
            return;
        }

        log.info("Checking {} asynchronous requests...");

        final Iterator<TaskRequest> iter = queue.iterator();
        while (iter.hasNext()) {
            final TaskRequest r = iter.next();
            if (r.isReady()) {
                log.info("Processing task {}...", r.taskType);
                // remove from the queue
                iter.remove();
                // run
                r.process();
            }
        }
    }
}
