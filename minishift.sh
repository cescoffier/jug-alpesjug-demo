#!/usr/bin/env bash
minishift start --cpus=4 --memory=6144 \
     --deploy-registry=true \
     --deploy-router=true
eval $(minishift docker-env)
minishift console