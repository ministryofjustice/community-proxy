#!/bin/bash

JAVA_OPTS="-Djavax.net.ssl.trustStore=/app/trusted.jks \
           -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} \
           -Djavax.net.ssl.trustStoreType=jks \
           -Dcom.sun.management.jmxremote.local.only=false \
           -Djava.security.egd=file:/dev/./urandom"

exec java -XX:+PrintFlagsFinal ${JAVA_OPTS} -jar /app/app.jar
