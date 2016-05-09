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

### Docker

```bash
git clone git://gerrit.ovirt.org/ovirt-vdsmfake.git
docker build -t vdsmfake .
docker run --rm -p 8080:8080 -p54321:54321 vdsmfake
```

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

### Standalone

The *maven-tomcat7-plugin* can generate a standalone war file for you which
will unclude a Tomcat7 server.  First create the *standalone.jar* file:

```bash
mvn package
```

Then run the application:

```bash
java -jar target/standalone.jar -DcacheDir=target/fakevdsm/cache -DlogDir=target/fakevdsm/log
```

### WAR

Run `mvn package` and copy the file *target/vdsmfake.war* into the deployment
folder of your favourite application server.

Default values for *cacheDir* and *logDir* are:
* /var/log/vdsmfake/cache
* /var/log/vdsmfake/log

### Development

Executing `mvn jetty:run` is enough. You can find the logs and the cached
entities inside of  `${project.basedir}/target/fakevdsm`.

## Maven commands
* Generate WAR and standalone archive: mvn package
* Run sample web server: mvn jetty:run

### Monitoring

To make it easy to see if performance test results for ovirt-engine are tainted
by this application, all JSON requests are monitored.
First, to see if the response preparations from vdsmfake are reasonable fast,
they are monitored. These metrics have the postfix **.Prepare**.
Second, to see how fast the data is transfered and accepted by ovirt-engine,
the send time is monitored. Metrics representing the send time have the postfix
**.Send**.

Hystrix Metrics can be accessed on http://localhost:54322/hystrix.stream with
Jetty and on http://localhost:8080/hystrix.stream in the standalone version
with Tomcat.

Further all metrics are exposed in 'com.netflix.servo' in JMX. They only become
visible **after** the first hystrix command was executed.

Finally exporting metrics to Graphite is possible too. By default it is disabled.
Setting the sytem property `graphite.url` to the graphite destionation server
enables the export mechanism. With the system property `graphite.interval` the
export interval in seconds can be specified. By default the export will happen
every 15 seconds. For example

```bash
mvn clean jetty:run -Dgraphite.url=localhost:2003 -Dgraphite.interval=20
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
