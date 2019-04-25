#!/bin/bash

TRUST_STORE_PASSWORD=D0n0tT3ll

certCount=0
files=`find . -name "*.crt"` 2> /dev/null
for filename in $files
do
   certCount=`$countCount+1` 
   aliasName="trust-$certCount"
   keytool -noprompt -storepass ${TRUST_STORE_PASSWORD} -keystore trusted.jks -importcert -file $filename -alias $aliasName
done

if [ $certCount -gt 0 ]
then
    JAVA_OPTS="-Djavax.net.ssl.trustStore=trusted.jks -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} -Djavax.net.ssl.trustStoreType=jks"
fi

exec java -XX:+PrintFlagsFinal ${JAVA_OPTS} \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Djava.security.egd=file:/dev/./urandom \
  -jar /app/app.jar
