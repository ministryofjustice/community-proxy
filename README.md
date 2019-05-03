# Community API proxy

This API accepts and authorises requests on behalf of the Community API and forwards only for authenticated clients.

The authentication process involves checking the JWT token provided by the client in the Authorization header, checking that the token
has the correct signature (ie. issued by the MOJ oauth2 service) and containing the role ROLE_COMMUNITY.

The service offers the following endpoints:

`List<Offenders> GET /communityapi/api/staff/staffCode/{staffCode}/managedOffenders`

`ResponsibleOfficer GET /communityapi/api/offenders/nomsNumber/{nomsId}/responsibleOfficer`

`String GET /communityapi/api/remote-status`

`String GET /communityapi/health`

The full swagger documentation (in T2) can be found here:

`https://community-api-t2.hmpps.dsd.io/communityapi/swagger-ui.html`

There is a small amount of translation of requests within the proxy application but generally it is a very thin layer such that
when the Community API is migrated to a more public cloud platform the clients will be able to consume resources directly with 
only a very small amount of rework to these requests.

# Tools Used

`Oracle Java JDK v11.x.x`
`Gradle v.5.x.x`
`Lombok 1.18.x`
`SpringBoot v.2.11`
`Mockito`
`Docker v18.x`
`CircleCI v2.x`
`Spring Security`
`Swagger 2.9.x`

# Commandline build, test and assemble

 `$ ./gradlew clean test assemble`

# Pipeline

There is a .circleci/config.yml file which defines the workflow steps tracking the master branch in GitHub.

# Curl Examples (environment-specific)

Request (T2 only):

`curl -X GET -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImF1dGhfc291cmNlIjoibm9uZSIsImV4cCI6MTg4MTg5MzM5MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9TWVNURU1fVVNFUiIsIlJPTEVfR0xPQkFMX1NFQVJDSCIsIlJPTEVfQ09NTVVOSVRZIiwiUk9MRV9MSUNFTkNFX1JPIl0sImp0aSI6ImMyNTdkYTMwLTUxNTgtNDM0Ni04NDNhLTU0NmE3ODA3ZjJiMiIsImNsaWVudF9pZCI6ImxpY2VuY2VzYWRtaW4ifQ.AV1qmGa8p5YkvVPCqNtHVEJ-Mse3J9CCdqYmtSz_VK8Mqdw26EJIczQSQRW3UFe5G78WST4u1GA9XQUKykxnh9dlAJpPs4p4YYEOT8MHIfmF7YCRKea-hZkU4FI_L2Rmjnfu1XOvA3LilMEWyl1QTkzjS22GLp7C9oWmfnk1pRrBiiG-kr5Q4S8jgfvje0GNBQQkFWJo7E3QlMHoH2EP9ufRgcEycNZ4qcmZ6vF_-ilcY-dDsCn9CspXPeAD8N3i7zkM-6h14T92xf0Is4AIigqNzHPBPJbDsEzz9dacgtFvepldpo_2VP2HMnDc2Zm7TLt0asgNItgR30fMPl8uFw" \
https://community-api-t2.hmpps.dsd.io/communityapi/api/offenders/nomsNumber/888/responsibleOfficers`

Response: 

`{"username":"JMJARRE1","staffCode":"AA999B","forenames":"Jean Michel","surname":"Jarre"}`

Request (T2 only):

`curl -X GET -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImF1dGhfc291cmNlIjoibm9uZSIsImV4cCI6MTg4MTg5MzM5MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9TWVNURU1fVVNFUiIsIlJPTEVfR0xPQkFMX1NFQVJDSCIsIlJPTEVfQ09NTVVOSVRZIiwiUk9MRV9MSUNFTkNFX1JPIl0sImp0aSI6ImMyNTdkYTMwLTUxNTgtNDM0Ni04NDNhLTU0NmE3ODA3ZjJiMiIsImNsaWVudF9pZCI6ImxpY2VuY2VzYWRtaW4ifQ.AV1qmGa8p5YkvVPCqNtHVEJ-Mse3J9CCdqYmtSz_VK8Mqdw26EJIczQSQRW3UFe5G78WST4u1GA9XQUKykxnh9dlAJpPs4p4YYEOT8MHIfmF7YCRKea-hZkU4FI_L2Rmjnfu1XOvA3LilMEWyl1QTkzjS22GLp7C9oWmfnk1pRrBiiG-kr5Q4S8jgfvje0GNBQQkFWJo7E3QlMHoH2EP9ufRgcEycNZ4qcmZ6vF_-ilcY-dDsCn9CspXPeAD8N3i7zkM-6h14T92xf0Is4AIigqNzHPBPJbDsEzz9dacgtFvepldpo_2VP2HMnDc2Zm7TLt0asgNItgR30fMPl8uFw" \
https://community-api-t2.hmpps.dsd.io/communityapi/api/staff/staffCode/888/managedOffenders`

Response: 

` [{"offenderNo":"CT800X"},{"offenderNo":"CR811Y"}]`

Request (T2 only):

`curl -X GET https://community-api-t2.hmpps.dsd.io/communityapi/health`

Response:

`{ "status":"UP"}`


# Properties to Override in non-local Environments

The base64-encoded public key of the oauth signing server for this environment:
 
`JWT_PUBLIC_KEY=X4H4H4H3h3...` (the public key of the token provider in the environment) 
 
 The password for the SSL trusted certificate store:
 
`TRUST_STORE_PASSWORD=secret` (the password to use for the SSL trust store)

 The base endpoint for the Delius API(e.g https://oasys400.noms.gsi.gov.uk):

`DELIUS_ENDPOINT_URL=https://host:port`

 The username to login to the Delius API and retrieve a token:

`DELIUS_USERNAME=DeliusAdminUserName`

 The FQDN, port and IP address for the Delius API host (used in the docker run command):

`DELIUS_NAME_IP_MAP=ndseis.ad.nps.internal:443:10.162.217.15`


# Trusted Certificates

The docker images is built to contain a trust store that is populated with the certificates
which will be trusted by the proxy. At present this contains the current X.509 certificates
for two environments.

When these certificates change the keystore will need to be updated with the new host, intermediate 
or CA root certificates as appropriate.

There is a script in the ${project-root}/keystores directory for recreating this keystore The 
docker build process will ensure it is included in the image produced.


# The Commmunity API

The Community API is deployed by Tolomy and resides in a private Uk Cloud network and infrastructure.
The following URLs are provided : 

`From t2pml0007 (stage env) : $ curl -v -k https://oasys400.noms.gsi.gov.uk/api/health`
    
`From pdpml00025 (production) : $curl -v --resolve ndseis.ad.nps.internal:443:10.162.217.15 \
                                      --cacert ndseis-ad-nps-internal.crt https://ndseis.ad.nps.internal/api/health`
     

# Docker

The application listens on port 8080 within the container and the docker run command maps this to 8081/tcp on the host.
 
To build & push the docker image to Docker Hub: 

`$ docker build -t mojdigitalstudio/community-proxy:latest .`

`$ docker login`

`$ docker push mojdigitalstudio/community-proxy:latest`
 
To run the container locally, expose 8081 to the local host and resolve host names use :
  
`$ docker pull mojdigitialstudio/community-proxy:latest`

`$ docker run -p 8081:8080 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest`

To run in staging (on t2pml0007) :

`$ docker run -p 8081:8080 --add-host=oasys400.noms.gsi.gov.uk:10.162.216.115 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest`

In production:

`$ docker run -p 8081:8080 --add-host=ndseis.ad.nps.internal:10.162.217.15 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest`
`

# IntelliJ setup

- Install jdk 11
- Enable Gradle using jdk 11
- Set jdk11 in project structure
- Enable the lombok plugin in IntelliJ and restart
- Enable annotation Processing at "Settings > Build > Compiler > Annotation Processors"
- Ensure commandline and IntelliJ build project and pass all tests

