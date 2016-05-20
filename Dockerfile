FROM fedora:23

ENV VERSION master

MAINTAINER "Roman Mohr" <rmohr@redhat.com>

RUN dnf -y install java-1.8.0-openjdk-headless && dnf clean all

RUN dnf --best --allowerasing -y install java-1.8.0-openjdk-devel maven tar nss && \
    curl -LO https://github.com/oVirt/ovirt-vdsmfake/archive/$VERSION.tar.gz#/ovirt-vdsmfake-$VERSION.tar.gz && \
    tar xf ovirt-vdsmfake-$VERSION.tar.gz && cd ovirt-vdsmfake-$VERSION && \
    mvn clean package && cp target/standalone.jar /vdsmfake.jar && \
    mkdir -p /var/cache/vdsmfake && mkdir -p /var/log/vdsmfake  && cd .. && \
    rm -rf ~/.m2 && \
    dnf -y remove java-1.8.0-openjdk-devel maven tar && dnf clean all

COPY src/main/resources/log4j-stdout.xml /log4j-stdout.xml

ENTRYPOINT ["/usr/bin/java", "-jar", "/vdsmfake.jar", "-Dlog4j.configuration=file:/log4j-stdout.xml", "-DcacheDir=/var/cache/vdsmfake", "-Dfake.host=0.0.0.0" ]

EXPOSE 54321
