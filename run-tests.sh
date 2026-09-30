#!/bin/bash

set -e

if [ -z "$1" ]; then
    echo "Usage: $0 {auth|users|mvc||firebase|media|notifications|rbac}"
    exit 1
fi

case "$1" in
    auth)
        MODULE="gossamercms-auth"
        ;;
    firebase)
        MODULE="gossamercms-firebase"
        ;;
    media)
        MODULE="gossamercms-media"
        ;;
    notifications)
        MODULE="gossamercms-notifications"
        ;;
    rbac)
        MODULE="gossamercms-rbac"
        ;;
    users)
        MODULE="gossamercms-users"
        ;;
    mvc)
        MODULE="gossamercms-mvc"
        ;;
    *)
        echo "Unknown module: $1"
        echo "Usage: $0 {auth|users|mvc||firebase|media|notifications|rbac}"
        exit 1
        ;;
esac

mvn -pl "$MODULE" \
    -Dtest=GenericEndpointTest \
    -Dendpoint.test.base-url=http://localhost:8080 \
    -Dsurefire.failIfNoSpecifiedTests=false \
    test