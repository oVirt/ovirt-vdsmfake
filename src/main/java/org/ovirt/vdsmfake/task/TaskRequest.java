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

import org.ovirt.vdsmfake.domain.Host;
import org.ovirt.vdsmfake.domain.Task;
import org.ovirt.vdsmfake.domain.VM;
import org.ovirt.vdsmfake.domain.VM.VMStatus;
import org.ovirt.vdsmfake.domain.VdsmManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class TaskRequest {
    private static final Logger log = LoggerFactory.getLogger(TaskRequest.class);

    final TaskType taskType;
    final long tm = System.currentTimeMillis();
    final long targetTm;
    final Object target;

    public TaskRequest(TaskType taskType, long delay, Object target) {
        this.taskType = taskType;
        this.target = target;
        targetTm = tm + delay;
    }

    public boolean isReady() {
        final long currentTime = System.currentTimeMillis();
        if (currentTime <= targetTm) {
            return false;
        }

        return true;
    }

    public void process() {
        try {
            VM vm = null;
            Task task = null;

            switch (taskType) {
            case START_VM:
                vm = (VM) target;
                vm.setStatus(VMStatus.WaitForLaunch);
                log.info("VM {} set to Wait For Launch state.", vm.getId());
                break;

            case START_VM_POWERING_UP:
                vm = (VM) target;
                vm.setStatus(VMStatus.PoweringUp);
                log.info("VM {} set to Powering Up state.", vm.getId());
                break;

            case START_VM_AS_UP:
                vm = (VM) target;
                vm.setStatus(VMStatus.Up);
                // store
                updateHost(vm.getHost());
                log.info("VM {} set to Up state.", vm.getId());
                break;

            case FINISH_MIGRATED_FROM_VM:
                vm = (VM) target;
                vm.setStatus(VM.VMStatus.Down);
                vm.setForDelete(true);
                // store
                updateHost(vm.getHost());
                log.info("Migrating VM {} finished on source host {}", vm.getId(), vm.getHost().getName());
                break;

            case FINISH_MIGRATED_FROM_VM_REMOVE_FROM_HOST:
                vm = (VM) target;
                vm.getHost().getRunningVMs().remove(vm.getId());
                // store
                updateHost(vm.getHost());
                log.info("Migrating VM {} removed from source host {}", vm.getId(), vm.getHost().getName());
                break;

            case FINISH_MIGRATED_TO_VM:
                vm = (VM) target;
                vm.setStatus(VM.VMStatus.Up);
                // store
                updateHost(vm.getHost());
                log.info("Migrating VM {} set to Up state on destination host {}", vm.getId(), vm.getHost().getName());
                break;

            case SHUTDOWN_VM:
                vm = (VM) target;
                vm.setStatus(VM.VMStatus.PoweredDown);
                vm.getHost().getRunningVMs().remove(vm.getId());
                // store
                updateHost(vm.getHost());
                log.info("VM {} set to Down state.", vm.getId());
                break;

            case FINISH_START_SPM:
                task = (Task) target;
                Host host = (Host) task.getTarget();
                host.setSpmId(1);
                host.setSpmLver(0);
                host.setSpmStatus(Host.SpmStatus.ACQUIRED); // SPM
                log.info("SPM started.");
                task.setFinished(true);
                break;

            case FINISH_CREATE_VOLUME:
                task = (Task) target;
                log.info("Volume created, task: {}", task.getId());
                task.setFinished(true);
                break;

            case FINISH_REMOVE_VOLUME:
                task = (Task) target;
                log.info("Volume removed, task: {}", task.getId());
                task.setFinished(true);
                break;

            default:
                log.error("Unhandled status detected.");
                break;
            }
        } catch (Exception e) {
            log.error("Error during request processing", e);
        }
    }

    private void updateHost(Host host) {
        VdsmManager.getInstance().updateHost(host);
    }
}
