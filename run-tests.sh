#!/bin/bash

set -e

if [ -z "$1" ]; then
    echo "Usage: $0 {auth|users|mvc|firebase|media|notifications|rbac}"
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
        echo "Usage: $0 {auth|users|mvc|firebase|media|notifications|rbac}"
        exit 1
        ;;
esac

ENDPOINT_DIR="$MODULE/src/test/resources/endpoints"
if [ ! -d "$ENDPOINT_DIR" ]; then
    echo "No endpoint test directory found for module: $MODULE"
    exit 1
fi

mapfile -t ENDPOINT_FILES < <(
    find "$ENDPOINT_DIR" -maxdepth 1 -type f -name '*.json' -printf 'classpath:endpoints/%f\n' | sort
)

if [ ${#ENDPOINT_FILES[@]} -eq 0 ]; then
    echo "No endpoint test files found for module: $MODULE"
    exit 1
fi

ENDPOINT_TEST_FILES=$(IFS=,; echo "${ENDPOINT_FILES[*]}")

mvn -pl "$MODULE" -am \
    -DskipTests \
    install

mvn -pl "$MODULE" \
    -Dtest=GenericEndpointTest \
    -Dendpoint.test.base-url=http://localhost:8080 \
    -Dendpoint.test.files="$ENDPOINT_TEST_FILES" \
    -Dsurefire.failIfNoSpecifiedTests=false \
    test
