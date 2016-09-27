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
This file includes routines that are used to build a scale environment
by creating objects like DC/Cluster/Host/Storage/VM/Disk/Nic/MAC Pool
using the Ovirt Python SDK against faked-vdsm
"""

import logging

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


# Get the reference to the data centers service:
dcs_service = connection.system_service().data_centers_service()
# Get the reference to the clusters service:
clusters_service = connection.system_service().clusters_service()
# Get the reference to the hosts service:
hosts_service = connection.system_service().hosts_service()
# Get the reference to the storage domains service:
sds_service = connection.system_service().storage_domains_service()
# Get the reference to the "vms" service:
vms_service = connection.system_service().vms_service()
# Get the reference to the service that manages the MAC address pools:
pools_service = connection.system_service().mac_pools_service()


def add_dc(pname, pdescription, plocal, pmajor, pminor):
    print('Adding DC : ' + pname + '...')
    dc = dcs_service.add(
        types.DataCenter(
            name=pname,
            description=pdescription,
            local=plocal,
            version=types.Version(major=pmajor,minor=pminor),
        ),
    )


def add_cluster(pname, pdescription, ptype, pdc):
    print('Adding Cluster : ' + pname + '...')
    clusters_service.add(
        types.Cluster(
            name=pname,
            description=pdescription,
            cpu=types.Cpu(
                architecture=types.Architecture.X86_64,
                type=ptype,
            ),
            data_center=types.DataCenter(
                name=pdc,
            ),
        ),
    )

# Use this for 4.1 and up

def add_mac_pool_to_cluster(pname, pfrom, pto, pcluster):

    print('Adding MAC Pool : ' + pname + 'to Cluster...')
    # Add a new MAC pool:
    pool = pools_service.add(
        types.MacPool(
            name=pname,
            ranges=[
                types.Range(
                    from_=pfrom,
                    to=pto,
                ),
            ],
        ),
    )

    # Find the cluster:
    cluster = clusters_service.list(search='name=' + pcluster)[0]

    # Find the service that manages the cluster, as we need it in order to
    # do the update:
    cluster_service = clusters_service.cluster_service(cluster.id)

    # Update the cluster so that it uses the new MAC pool:
    cluster_service.update(
        types.Cluster(
            mac_pool=types.MacPool(
                id=pool.id,
            ),
        ),
    )


def add_mac_pool_to_dc(pname, pfrom, pto, pdc):

    print('Adding MAC Pool : ' + pname + 'to DC ...')
    # Add a new MAC pool:
    pool = pools_service.add(
        types.MacPool(
            name=pname,
            ranges=[
                types.Range(
                    from_=pfrom,
                    to=pto,
                ),
            ],
        ),
    )

    # Find the DC:
    dc = dcs_service.list(search='name=' + pdc)[0]

    # Find the service that manages the DC, as we need it in order to
    # do the update:
    dc_service = dcs_service.data_center_service(dc.id)

    # Update the DC so that it uses the new MAC pool:
    dc_service.update(
        types.DataCenter(
            mac_pool=types.MacPool(
                id=pool.id,
            ),
        ),
    )

def add_host(pname, pdescription, paddress, proot_password, pcluster, wait_for_up):
    print('Adding Host : ' + pname + '...')
    host = hosts_service.add(
        types.Host(
            name=pname,
            description=pdescription,
            address=paddress,
            root_password=proot_password,
            cluster=types.Cluster(
                name=pcluster,
            ),
        ),
    )
    if wait_for_up:
        host_service = hosts_service.host_service(host.id)
        track_status(host_service, types.HostStatus.UP, 1)



def add_storage(pname, pdescription, phost, paddress, ppath):
    print('Adding Storage : ' + pname + '...')
    sd = sds_service.add(
        types.StorageDomain(
            name=pname,
            description=pdescription,
            type=types.StorageDomainType.DATA,
            host=types.Host(
                name=phost,
            ),
            storage=types.HostStorage(
                type=types.StorageType.NFS,
                address=paddress,
                path=ppath,
            ),
        ),
    )

    sd_service = sds_service.storage_domain_service(sd.id)
    track_status(sd_service, types.StorageDomainStatus.UNATTACHED, 1)


def attach_storage(psearch_storage, psearch_dc):
    sd = sds_service.list(search=psearch_storage)[0]
    dc = dcs_service.list(search=psearch_dc)[0]
    dc_service = dcs_service.data_center_service(dc.id)
    attached_sds_service = dc_service.storage_domains_service()
    attached_sds_service.add(
        types.StorageDomain(
            id=sd.id,
        ),
    )
    attached_sd_service = attached_sds_service.storage_domain_service(sd.id)
    track_status(attached_sd_service.get, types.StorageDomainStatus.ACTIVE, 1)

def add_vm(pname, pcluster, ptemplate):
    print('Adding VM : ' + pname + '...')
    vms_service.add(
        types.Vm(
            name=pname,
            cluster=types.Cluster(
                name=pcluster,
            ),
            template=types.Template(
                name=ptemplate,
            ),
        ),
    )

def add_disk(pname, pdiskname, pdisk_description, psd, pbootable, psize, wait_for_up):
    print('Adding Disk : ' + pdiskname + ' to VM ' + pname + '...')
    vm = vms_service.list(search='name=' + pname)[0]
    disk_attachments_service = vms_service.vm_service(vm.id).disk_attachments_service()
    disk_attachment = disk_attachments_service.add(
        types.DiskAttachment(
            disk=types.Disk(
                name=pdiskname,
                description=pdisk_description,
                format=types.DiskFormat.COW,
                provisioned_size=psize,
                storage_domains=[
                    types.StorageDomain(
                        name=psd,
                    ),
                ],
            ),
            interface=types.DiskInterface.VIRTIO,
            bootable=pbootable,
        ),
    )
    if wait_for_up:
        # Wait till the disk is OK:
        disks_service = connection.system_service().disks_service()
        disk_service = disks_service.disk_service(disk_attachment.disk.id)
        track_status(disk_service, types.DiskStatus.OK, 1)

def add_nic(pname, pnetwork, pnicname, pnic_description):
    print('Adding Nic : ' + pnicname + ' to VM ' + pname + '...')
    vm = vms_service.list(search='name=' + pname)[0]
    # In order to specify the network that the new interface will be
    # connected to we need to specify the identifier of the virtual network
    # interface profile, so we need to find it:
    profiles_service = connection.system_service().vnic_profiles_service()
    profile_id = None
    for profile in profiles_service.list():
        if profile.name == pnetwork:
            profile_id = profile.id
            break

    # Locate the service that manages the network interface cards of the
    # virtual machine:
    nics_service = vms_service.vm_service(vm.id).nics_service()

    # Use the "add" method of the network interface cards service to add the
    # new network interface card:
    nics_service.add(
        types.Nic(
            name=pnicname,
            description=pnic_description,
            vnic_profile=types.VnicProfile(
                id=profile_id,
            ),
        ),
    )

def build_scale_env():
    add_dc('mydc', 'My data center', False, 4, 0)
    add_cluster('mycluster', 'My cluster', 'Intel Conroe Family', 'mydc')
    # This is for version 4.0 and below
    add_mac_pool_to_dc('mypool', '00:b0:00:00:00:00',  '00:b0:0f:00:00:00', 'mydc')
    # in case of 4.1 version and above use the following to attach a mac pool to the cluster
    #add_mac_pool_to_cluster('mypool', '00:b0:00:00:00:00',  '00:b0:0f:00:00:00', 'mycluster')

    for host_num in range(1,51):
        name = 'myhost' + str(host_num)
        add_host(name, name, name, 'qum5net', 'mycluster', host_num==1)
    add_storage('mydata1', 'My data', 'myhost1', 'server0.example.com', '/nfs/ovirt/40/mydata1')
    attach_storage('name=mydata1', 'name=mydc')
    for vm_num in range(1,1501):
        vm_name = 'myvm' + str(vm_num)
        add_vm(vm_name, 'mycluster', 'Blank')
        for disk_num in range(1,3):
            add_disk(vm_name, 'mydisk' + str(vm_num) + str(disk_num), 'My disk', 'mydata1', disk_num==1, 2**30, False)
        for nic_num in range(1,2):
            add_nic(vm_name,'Default', 'mynic' + str(vm_num) + str(nic_num), 'My Nic')

build_scale_env()

# Close the connection to the server:
connection.close()

