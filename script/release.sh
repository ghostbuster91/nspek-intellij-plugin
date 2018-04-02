#!/bin/bash
# USAGE: ./release.sh 1.1.1
set -e
test $# -eq 1 || echo "USAGE: ./release.sh 1.1.1"

DIR=$(dirname $0)
VERSION_NAME=$1
git stash
git checkout master
git merge --no-ff --no-edit develop
git push
git tag v$VERSION_NAME
git push --tags
git checkout develop