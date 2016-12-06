#!/usr/bin/env bash

RED='\033[0;31m'
NC='\033[0m' # No Color
YELLOW='\033[0;33m'
BLUE='\033[0;34m'

echo -e "${BLUE}Creating project $1 ${NC}"
oc new-project $1
echo -e "${BLUE}Adding permissions to $1 ${NC}"
oc policy add-role-to-user view admin -n $1
oc policy add-role-to-group view system:serviceaccounts -n $1
echo -e "${BLUE}Done ! ${NC}"
