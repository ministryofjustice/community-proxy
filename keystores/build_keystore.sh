#/bin/bash

cp $JAVA_HOME/lib/security/cacerts .
if [ -f cacerts ]
then
  echo "Copying cacerts as trusted.jks"
  mv cacerts trusted.jks
else
  echo "No cacerts file found - JAVA_HOME set?"
  exit 0
fi

while :
do
   echo -n "Enter the keystore password: "
   read STOREPASS
   if [ -z $STOREPASS ]
   then 
     echo "You must enter something.."
     break
   fi
done

# Change the default password to the entered one
keytool -storepasswd -new $STOREPASS -keystore trusted.jks -storepass changeit

# Import the Tolomy certificates to trust
keytool -noprompt -storepass $STOREPASS -keystore trusted.jks -importcert -file ndseis-ad-nps-internal.crt -alias ndseis
keytool -noprompt -storepass $STOREPASS -keystore trusted.jks -importcert -file oasys400-noms-gov-uk.crt -alias oasys400

echo "Created keystore trusted.jks"

# End
