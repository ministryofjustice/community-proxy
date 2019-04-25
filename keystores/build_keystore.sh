#/bin/bash

while :
do
   echo -n "Enter the keystore password: "
   read STOREPASS
   if [ ! -z $STOREPASS ]
   then 
     echo "You must enter something.."
     break
   fi
done

keytool -noprompt -storepass $STOREPASS -keystore trusted.jks -importcert -file ndseis-ad-nps-internal.crt -alias ndseis
keytool -noprompt -storepass $STOREPASS -keystore trusted.jks -importcert -file oasys400-noms-gov-uk.crt -alias oasys400

echo "Created keystore trusted.jks"

# End
