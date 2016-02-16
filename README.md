## Introduction
VDSM is a daemon component written in Python required by oVirt-Engine (Virtualization Manager), which runs on Linux hosts and manages and monitors the host's storage, memory and networks as well as virtual machine creation/control, statistics gathering, etc.
'''VDSM Fake''' is a support application framework for oVirt Engine project. It is a Java web application which enables to simulate selected tasks of real VDSM. But, tens or hundreds of simulated Linux hosts and virtual machines can be reached with very limited set of hardware resources.
The aim is to get marginal performance characteristics of oVirt Engine JEE application (JBoss) and its repository database (PostgreSQL), but also network throughput, etc.

## Technology
The basic idea is that the fake host addresses must resolve to a single IP address (127.0.0.1 is also possible for all-in-one performance testing server configuration). Standard HTTP port 54321 must be accessible from the Engine. You can use /etc/hosts file on the server with oVirt-Engine or company DNS server. Instead of host IP address it is needed to specify fake host name.
Apache XML-RPC library is a core technology for the Engine and VDSM communication.
Many configured entities must be persisted after their creation. Simple Java object serialization is used for this
purpose. They are stored in `/var/log/fakevdsm/cache` by default. Set the system porperty `${cacheDir}` to customize
the location.

## Quick Start

### Prepare ovirt-engine

```bash
sudo -i -u postgres
export ENGINE_DB=dbname
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'false' WHERE option_name = 'SSLEnabled';"
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'false' WHERE option_name = 'EncryptHostCommunication';"
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'false' where option_name = 'InstallVds'"
```

This disables SSL encryption and skips installation when adding VDSM hosts. Restart the engine after the values were
set.

### JSON-RPC (ovirt-engine >= 3.6)

```bash
git clone git://gerrit.ovirt.org/ovirt-vdsmfake.git
cd ovirt-vdsmfake
mvn jetty:run
```

### XML-RPC (ovirt-engine < 3.6)

```bash
git clone git://gerrit.ovirt.org/ovirt-vdsmfake.git
cd ovirt-vdsmfake
mvn jetty:run -DjsonListenPort=54322 -DvdsmPort=54321
```

Here we are flipping the XML-RPC and the JSON-RPC ports, so that the engine will find the XML-RPC server on the default
vdsm port `54321`.

### Create fake host names

```bash
sudo -i
for i in `seq 0 10`; do echo 127.0.0.1 test$i >> /etc/hosts; done
```

Use `dnsmasq` for more dynamic approach to make every X.vdsm.simulator resolve to an IP:

```bash
dnsmasq --address=/vdsm.simulator/127.0.0.1
```

Add 127.0.0.1 as a dns server:
```bash
cat /etc/resolv.conf
nameserver 127.0.0.1
```

### Add the fake hosts

```bash
function add_host {
  xml="<host><name>$2</name><address>$2</address><root_password>test</root_password></host>"
  curl -H "Accept: application/json" -H "Content-type: application/xml" -X POST --user $1 http://localhost:8080/ovirt-engine/api/hosts --data "$xml"
}

for i in `seq 0 10`; do add_host admin@internal:mypwd test$i; done
```

## Functionality
The application runs in 2 modes:
* simulation
* proxy to real VDSM

All XML/Json requests/responses are optionally logged into the default path `/var/log/vakevdsm/`. Set the
system property ${logDir} to customize the location. Log4j logs into this directory too.

## Supported methods
* create hosts
* create/attach/activate DATA/EXPORT/ISO NFS storage domains
* create VM from iso (+ create network, create volume)
* run/shutdown VM
* migrate VM

## Project
VDSM Fake is a Maven configured project. Source code:
* git clone git://gerrit.ovirt.org/ovirt-vdsmfake.git

## Run the project
Executing `mvn jetty:run` is enough. You can find the logs and the cached entities inside of  `${project.basedir}/target/fakevdsm`.

## Installation
Required directories (set RW access):
* /var/log/vdsmfake
* /var/log/vdsmfake/cache

Maven commands:
* Generate WAR file: mvn install
* Run sample web server: mvn jetty:run

