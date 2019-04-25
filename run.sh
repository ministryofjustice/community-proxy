#!/bin/bash

# JAVA_OPTS="-Djavax.net.ssl.trustStore=trusted.jks -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} -Djavax.net.ssl.trustStoreType=jks"
JAVA_OPTS="-Djavax.net.ssl.trustStore=trusted.jks -Djavax.net.ssl.trustStorePassword=D0N0tT3ll -Djavax.net.ssl.trustStoreType=jks"

exec java -XX:+PrintFlagsFinal ${JAVA_OPTS} \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Djava.security.egd=file:/dev/./urandom \
  -jar /app/app.jar
