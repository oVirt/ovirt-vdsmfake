FROM centos:7

MAINTAINER "Roy Golan" <rgolan@redhat.com>

ARG release

COPY exported-artifacts/x86_64/ovirt-vdsmfake*.rpm /opt/artifacts/

RUN yum install -y \
    java-1.8.0-openjdk-headless \
    /opt/artifacts/ovirt-vdsmfake-*${release}*.rpm && \
    rm -rf /opt/artifacts && \
    yum clean all

EXPOSE 54321 8081
ENTRYPOINT /usr/bin/vdsmfake
