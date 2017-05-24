#!/bin/sh -xe

VERSION=latest
RELEASE=$(git describe --always)
ARTIFACT_DIR=exported-artifacts

trap popd 0
pushd $(dirname "$(readlink -f "$0")")/..

mkdir -p ${ARTIFACT_DIR}

# make sure we have maven 3.3 - wildfly-swarm must have it
curl -sSL http://apache.spd.co.il/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz | tar -xzv
export PATH=${PWD}/apache-maven-3.5.0/bin/:$PATH

# bulid rpm
./build-rpm.sh --define "_rpmdir ${ARTIFACT_DIR}" --define "_release ${RELEASE}"


# build docker
docker build -t vdsmfake:${VERSION}-${RELEASE} --build-arg release=${RELEASE} -f automation/Dockerfile .
docker save -o ${ARTIFACT_DIR}/vdsmfake-container-image.tar vdsmfake:${VERSION}-${RELEASE}
