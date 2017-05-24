#!/bin/bash

NAME=ovirt-vdsmfake
VERSION=${VERSION-1.0}

git ls-files | tar --files-from /proc/self/fd/0 -czf "${NAME}-${VERSION}.tar.gz"
rpmbuild -tb ${NAME}-${VERSION}.tar.gz --define "debug_package %{nil}" "$@"
