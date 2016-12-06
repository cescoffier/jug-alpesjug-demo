#!/usr/bin/env bash
DIR="$(dirname "$0")"
NAME=${PWD##*/}
echo "Adding readiness probe to $NAME (http://localhost:8080$1"
oc set probe dc/$NAME --readiness --get-url=http://:8080$2

echo "Adding liveness probe to $$NAME (http://localhost:8080$1"
oc set probe dc/$NAME --liveness --get-url=http://:8080$2
