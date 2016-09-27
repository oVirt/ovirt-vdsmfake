#
# ovirt-vdsm-fake ovirt vdsm fake
# Copyright (C) 2016 Red Hat, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
This file includes routines that are used to execute a scale environment
by executing operation periodically (like start/stop VMs)
using the Ovirt Python SDK against faked-vdsm
"""

import logging
import time

import ovirtsdk4 as sdk
import ovirtsdk4.types as types
from common import track_status

logging.basicConfig(level=logging.DEBUG, filename='test.log')

connection = sdk.Connection(
    url='https://laptop.emesika.com:8443/ovirt-engine/api',
    username='admin@internal',
    password='a',
    insecure=True,
    debug=True,
    log=logging.getLogger(),
)

# Get the reference to the "vms" service:
vms_service = connection.system_service().vms_service()


def run_vm(pname, wait_for_up):
    print('Running VM : ' + pname + '...')
    # Find the virtual machine:
    vm = vms_service.list(search='name=' + pname)[0]

    # Locate the service that manages the virtual machine, as that is where
    # the action methods are defined:
    vm_service = vms_service.vm_service(vm.id)

    # Call the "start" method of the service to start it:
    vm_service.start()

    if wait_for_up:
        # Wait till the virtual machine is up:
        track_status(vm_service, types.VmStatus.UP, 1)

def stop_vm(pname, wait_for_down):
    print('Stopping VM : ' + pname + '...')
    # Find the virtual machine:
    vm = vms_service.list(search='name=' + pname)[0]

    # Locate the service that manages the virtual machine, as that is where
    # the action methods are defined:
    vm_service = vms_service.vm_service(vm.id)

    # Call the "start" method of the service to start it:
    vm_service.stop()

    if wait_for_down:
        # Wait till the virtual machine is down:
        track_status(vm_service, types.VmStatus.DOWN, 1)


def run_vm_scenario(prepeat, pdelay):
    for times in range(1, prepeat+1):
        for vm_num in range(1,101):
            vm_name = 'myvm' + str(vm_num)
            vm = vms_service.list(search='name=' + vm_name)[0]
            vm_service = vms_service.vm_service(vm.id)
            if vm.status == types.VmStatus.DOWN:
                run_vm(vm_name, False)
            elif vm.status == types.VmStatus.UP:
                stop_vm(vm_name, False)
        time.sleep(pdelay)




run_vm_scenario(30,10)


# Close the connection to the server:
connection.close()

