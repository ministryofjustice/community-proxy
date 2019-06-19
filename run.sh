#!/bin/bash

JAVA_OPTS="-Djavax.net.ssl.trustStore=/app/trusted.jks \
           -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} \
           -Djavax.net.ssl.trustStoreType=jks \
           -Dcom.sun.management.jmxremote.local.only=false \
           -Ddelius.endpoint.url=${DELIUS_ENDPOINT_URL} \
           -Ddelius.api.username=${DELIUS_API_USERNAME} \
           -DAPPLICATION_INSIGHTS_IKEY=${APP_INSIGHTS_KEY} \
           -Djwt.public.key=${JWT_PUBLIC_KEY} \
           -Djava.security.egd=file:/dev/./urandom"

exec java -XX:+PrintFlagsFinal ${JAVA_OPTS} -jar /app/app.jar
