#!/bin/sh -x

VERSION=latest
ARTIFACT_DIR=exported-artifacts

trap popd 0
pushd $(dirname "$(readlink -f "$0")")/..

docker build -t vdsmfake:${VERSION} -f Dockerfile .

mkdir -p ${ARTIFACT_DIR}
docker save -o ${ARTIFACT_DIR}/vdsmfake-container-image.tar vdsmfake:${VERSION}



