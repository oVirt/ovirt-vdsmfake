#!/bin/bash -ex

NAME=ovirt-vdsmfake
VERSION=$(git describe --tags --always | cut -d "-" -f1)
RELEASE=$(git describe --tags --always | cut -d "-" -f2- | sed 's/-/_/')

git ls-files | tar --files-from /proc/self/fd/0 -czf "${NAME}-${VERSION}-${RELEASE}.tar.gz"
rpmbuild -tb ${NAME}-${VERSION}-${RELEASE}.tar.gz --define "debug_package %{nil}" "$@"
