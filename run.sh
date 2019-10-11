#!/bin/sh
exec java ${JAVA_OPTS} \
  -Djavax.net.ssl.trustStore=/app/trusted.jks \
  -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} \
  -Djavax.net.ssl.trustStoreType=jks \
  -Djava.security.egd=file:/dev/./urandom \
  -javaagent:/app/agent.jar \
  -jar /app/app.jar
