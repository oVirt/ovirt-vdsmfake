VDSM is a daemon component written in Python and required by oVirt-Engine (Virtualization Manager). Running on Linux hosts, VDSM manages and monitors the hosts' storage, memory and networks. It also handles virtual machine creation/control, statistics gathering, and more.
'''VDSM Fake''' is a support application framework for the oVirt Engine project. It is a Java web application, built using wildfly-swarm, and used to simulate selected tasks of the real VDSM. But, tens or hundreds of simulated Linux hosts and virtual machines can be reached with a very limited set of hardware resources.
The aim is to get marginal performance characteristics of oVirt Engine JEE application (JBoss) and its repository database (PostgreSQL), but also network throughput, etc.

## Technology
The basic idea is that the fake host addresses must resolve to a single IP address (127.0.0.1 is also possible for all-in-one performance testing server configuration). Standard HTTP port 54321 must be accessible from the Engine. You can use /etc/hosts file on the server with oVirt-Engine or company DNS server. Instead of a host IP address, a fake host name needs to be specified.
Many configured entities must be persisted after their creation. Simple Java object serialization is used for this
purpose. By default, they are stored in `/var/log/fakevdsm/cache`. Set the system property `${cacheDir}` to customize
the location.

## Quick Start

### Prepare ovirt-engine
This skips installation when adding VDSM hosts.
```bash
sudo -i -u postgres
export ENGINE_DB=dbname
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'false' where option_name = 'InstallVds';"
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'true' WHERE option_name = 'UseHostNameIdentifier';"
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = '0' WHERE option_name = 'HostPackagesUpdateTimeInHours';"
```

#### Disable SSL (Not Default!)
In case you need to disable SSL encryption, run the following queries (on engine):
```bash
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'false' WHERE option_name = 'SSLEnabled';"
psql $ENGINE_DB -c "UPDATE vdc_options set option_value = 'false' WHERE option_name = 'EncryptHostCommunication';"

Restart the engine after the values were set.

```

#### Work with SSl (default installation)
- In general, the following action will generate certs to vdsmfake.
- Make sure you do this in a protected directory, as the key should be in vdsm.

On vdsmfake machine:
```bash
cer_req_name="vdsmfake"

pkidir=/etc/pki/vdsmfake
keys="$pkidir/keys"
requests="$pkidir/requests"
mkdir -p "$keys" "$requests"
chmod 700 "$keys"

key="$keys/$cer_req_name.key"
req="$requests/$cer_req_name.req"

openssl genrsa -out "$key" -passout "pass:$pass" -des3 2048
openssl req -new -days 365 -key "$key" -out "$req" -passin "pass:$pass" -passout "pass:$pass" -batch -subj "/"

scp $req <ovirt_user>@<ovirt_host>/<ovirt_dir>/etc/pki/ovirt-engine/requests/

```

On ovirt-engine machine.
```bash
cer_req_name="vdsmfake"
domain=test.test.com

# Whatever you want
subject="/C=US/O=$domain/CN=something.$domain"
"<ovirt_engine_dir>/bin/pki-enroll-request.sh --name=\"$cer_req_name\" --subject=\"$subject\""

#The cert will be created in /etc/pki/ovirt-engine/certs/$cer_req_name.cer .
```

#### Large setups tweaks:
- quartz pool size
  This setting can be changed in ovirt-engine.xml.in and the option name is org.quartz.threadPool.threadCount
  Restart the engine after making this change.

- db connection pool size
  This setting can be changed in ovirt-engine.conf and the option name is ENGINE_DB_MAX_CONNECTIONS
  It requires additional changes in /var/lib/pgsql/data/postgresql.conf and the option name is max_connections.
  Restart postgresql and the engine after making the above changes.

## Run the project

Pick your method:
1. From source (mvn)
2. Standalone
3. Container

### From source (mvn)

```mvn wildfly-swarm:run```

### Standalone

The *__wildfly-plugin__* can generate a standalone uber-jar that includes wildfly and the vdsmfake in it(see wildfly-swarm)
Create the uber-jar and run it:
```bash
mvn wildfly-swarm:package
java -jar target/vdsmfake-swarm.jar
```

### Container

Official containers are created by jenkins.ovirt.org after each merge. Those containers will be pushed to docker.io
registry soon. Again, there are a few optons to run from container:

1. Use oVirt CI container produced by [jenkins job][jenkins_job]
```bash
wget http://jenkins.ovirt.org/job/ovirt-vdsmfake_master_build-artifacts-el7-x86_64/lastSuccessfulBuild/artifact/exported-artifacts/vdsmfake-container-image.tar
docker load -i vdsmfake-container-image.tar
```

2. Build your own
```bash
docker build -t vdsmfake github.com/ovirt/ovirt-vdsmfake
docker run --rm -p54322:54322 -p54321:54321 vdsmfake
```

[jenkins_job]: http://jenkins.ovirt.org/job/ovirt-vdsmfake_master_build-artifacts-on-demand-el7-x86_64/lastSuccessfulBuild/

### Create fake host names

```bash
sudo -i
for i in `seq 0 10`; do echo 127.0.0.1 test$i >> /etc/hosts; done
```

Use `dnsmasq` for a more dynamic approach to make every X.vdsm.simulator resolve to an IP:

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

All Json requests/responses are optionally logged into the default path `/var/log/vakevdsm/`. Set the
system property ${logDir} to customize the location. Log4j logs into this directory too.

## Supported methods
* create hosts
* create/attach/activate DATA/EXPORT/ISO NFS storage domains
* create VM from iso (+ create network, create volume)
* run/shutdown VM
* migrate VM

## Changing the simulated host architecture
VDSM fake can be used to simulate two architectures: ppc64 or x86_64.
To do so set the system property ${architectureType} to one of the following values: ppc64 or x86_64.
```bash
mvn wildfly-swarm:run -DarchitectureType=ppc64
```
This property is optional. The default architecture type is x86_64 and is set in web.xml.
If no architecture type is provided the default architecture will be used.

## Project
VDSM Fake is a Maven project.
* Clone it `git clone git://gerrit.ovirt.org/ovirt-vdsmfake.git`
* Hack it and `mvn wildfly-swarm:run` is enough to see the changes.
* Report [issues here](https://github.com/oVirt/ovirt-vdsmfake/issues)


Also:
* *logs* - under the current directory `vdsmfake.log` - change by passing -DlogDir=/path/
* persistence - simulated entities are kept under objectStore in binary format - change with `-DcacheDir=/path/``


### Monitoring

To make it easy to see if performance test results for ovirt-engine are tainted
by this application, JSON requests can be monitored using `-Dvdsmfake.commandExecutor=hystrix`.
First, to see if the response preparations from vdsmfake are reasonably fast,
they are monitored. These metrics have the postfix **.Prepare**.
Second, to see how fast the data is transfered and accepted by ovirt-engine,
the send time is monitored. Metrics representing the send time have the postfix
**.Send**.

Hystrix Metrics can be accessed on http://localhost:54322/hystrix.stream.

Further, all metrics are exposed in 'com.netflix.servo' in JMX. They only become
visible **after** the first hystrix command was executed.

Finally exporting metrics to Graphite is possible too. By default it is disabled.
Setting the sytem property `graphite.url` to the graphite destination server
enables the export mechanism. Use the system property `graphite.interval`to specify the export interval
(seconds). By default, the export will happen
every 15 seconds. For example:

```bash
mvn clean wildfly-swarm:run -Dvdsmfake.commandExecutor=hystrix -Dgraphite.url=localhost:2003 -Dgraphite.interval=20
```

exports hystrix metrics every 20 seconds to the graphite database at
`localhost:2003`.

An easy way to get Graphite and Grafana up and running is docker:

```bash
docker run -d -p 8070:80 -p 2003:2003 -p 8125:8125/udp -p 8126:8126 \
    --name grafana-dashboard choopooly/grafana-graphite
```

Graphite will listen on `localhost:2003` and Grafana at `localhost:8070`. The
metrics prefix is `vdsmfake`.

This application uses Netflix Servo for the export.
[Here](http://www.nurkiewicz.com/2015/02/storing-months-of-historical-metrics.html)
is a nice post about how to do the same thing with Dropwizard.

## Configuration

### Vdsmfake Configuration

The various simulation classes and verbs uses the application.conf shipped with the project.

Order of precedence:

- System property
- definition in `src/main/resources/application.conf`

`application.conf` format is in [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md)
 which is a nice, clean, and powerful super set of JSON

| property              | Type                     | Description                           |
|-----------------------|--------------------------|---------------------------------------|
| networkBridgeName     | String                   | The default network bridge name       |
| constantDelay         | long                     |                                       |
| randomDelay           | long                     |                                       |
| storageDelay          | Tuple (list of 2 values) |                                       |
| networkLoad           | Tuple (list of 2 values) |                                       |
| cpuLoad               | Tuple (list of 2 values) |                                       |
| memLoad               | Tuple (list of 2 values) |                                       |
| architectureType      | String                   | Simulate X86_64 or PPC                |
| cacheDir              | String                   | Where to store the simulation objects |
| jsonEvents            | boolean                  | Enable sending events through jsonrpc |
| jsonThreadPoolSize    |                          |                                       |
| eventsThreadPoolSize  |                          |                                       |
| eventSupportedMethods |                          |                                       |
| notLoggedMethods      |                          |                                       |
| jsonListenPort        |                          |                                       |
| jsonSecured           |                          |                                       |
| jsonHost              |                          |                                       |
| certspath             |                          |                                       |
| delayMinimum          |                          |                                       |
| emulatedMachines      | List                     | List of supported emulated machine    |

### Wildfly Swarm Configuration

Order of precedence:

- System property
- definition in `src/main/resources/project-defaults.yml`

[A reference to swarm configuration](https://reference.wildfly-swarm.io/configuration.html.)

| property                                   | Type                     | Description                           |
|--------------------------------------------|--------------------------|---------------------------------------|
| swarm.http.port                            | int                      | http listening port - default 8081    |
| swarm.logging.file-handlers.FILE.file.path | String                   | main log file                         |
