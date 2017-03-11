FROM centos:latest

MAINTAINER "Roman Mohr" <rmohr@redhat.com>
MAINTAINER "Roy Golan" <rgolan@redhat.com>

RUN yum install -y \
    centos-release-scl \
    java-1.8.0-openjdk-headless \
    java-1.8.0-openjdk-devel && \
    yum clean all

# Wildfly swarm needs mvn >= 3.2.1
RUN yum install -y rh-maven33 && \
    yum clean all

COPY . /usr/src/ovirt-vdsmfake/

RUN cd /usr/src/ovirt-vdsmfake && \
    scl enable rh-maven33 "mvn clean package" && \
    scl enable rh-maven33 "mvn wildfly-swarm:package" && \
    cp target/vdsmfake-swarm.jar /opt && \
    mkdir -p /var/cache/vdsmfake /var/log/vdsmfake

EXPOSE 54321 8081
ENTRYPOINT [ "/usr/bin/java", "-jar", "/opt/vdsmfake-swarm.jar", "-Dswarm.logging.file-handlers.FILE.file.path=/var/log/vdsmfake/vdsmfake.log", "-DcacheDir=/var/cache/vdsmfake", "-Dfake.host=0.0.0.0", "-DPS1=_" ]
