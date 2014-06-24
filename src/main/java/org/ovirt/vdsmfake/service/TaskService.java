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

import java.util.Map;

import org.ovirt.vdsmfake.domain.Task;

/**
 *
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TaskService extends AbstractService {

    final static TaskService instance = new TaskService();

    public static TaskService getInstance() {
        return instance;
    }

    public TaskService() {
    }

    public Map getTaskStatus(String taskUUID) {
        final Task task = getActiveHost().getRunningTasks().get(taskUUID);

        final Map resultMap = getOKStatus();
        if (task == null) {
            return resultMap;
        }

        if (!task.isFinished()) {
            resultMap.put("code", Integer.valueOf(0));
            resultMap.put("message", "Task is initializing");
            resultMap.put("taskState", "running");
            resultMap.put("taskStatus", "running");
            resultMap.put("taskResult", "");
            resultMap.put("taskID", taskUUID);
        } else {
            resultMap.put("code", Integer.valueOf(0));
            resultMap.put("message", "1 jobs completed successfully");
            resultMap.put("taskState", "finished");
            resultMap.put("taskStatus", "finished");
            resultMap.put("taskResult", "success");
            resultMap.put("taskID", taskUUID);
        }

        return resultMap;
    }

    public Map getAllTasksStatuses() {
        final Map resultMap = getOKStatus();

        final Map allTasksStatusMap = map();
        resultMap.put("allTasksStatus", allTasksStatusMap);

        for (Task task : getActiveHost().getRunningTasks().values()) {
            Map taskMap = map();

            if (!task.isFinished()) {
                taskMap.put("code", "0");
                taskMap.put("message", "Task is initializing");
                taskMap.put("taskState", "running");
                taskMap.put("taskStatus", "running");
                taskMap.put("taskResult", "");
                taskMap.put("taskID", task.getId());
            } else {
                taskMap.put("code", "0");
                taskMap.put("message", "1 jobs completed successfully");
                taskMap.put("taskState", "finished");
                taskMap.put("taskStatus", "finished");
                taskMap.put("taskResult", "success");
                taskMap.put("taskID", task.getId());
            }

            allTasksStatusMap.put(task.getId(), taskMap);
        }

        return resultMap;
    }

    public Map getAllTasksInfo() {
        final Map resultMap = getOKStatus();

        final Map allTasksStatusMap = map();
        resultMap.put("allTasksInfo", allTasksStatusMap);

        // TODO ?

        return resultMap;
    }

    public Map clearTask(String taskUUID) {
        final Task task = getActiveHost().getRunningTasks().get(taskUUID);

        if (task != null) {
            getActiveHost().getRunningTasks().remove(taskUUID);
        }

        return getOKStatus();
    }

    public Map stopTask(String taskUUID) {
        final Task task = getActiveHost().getRunningTasks().get(taskUUID);
        if (task != null) {
            task.setFinished(true);
        }

        return getOKStatus();
    }

    public Map revertTask(String taskUUID) {
        final Task task = getActiveHost().getRunningTasks().get(taskUUID);
        if (task != null) {
            task.setFinished(false);
        }

        // TODO ?

        return getOKStatus();
    }

}
