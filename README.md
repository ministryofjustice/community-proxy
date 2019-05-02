# Community API proxy

This API accepts and authorises requests on behalf of the Community API and forwards only for authenticated clients.

The authentication process involves checking the JWT token provided by the client in the Authorization header, checking that the token
has the correct signature (ie. issued by the MOJ oauth2 service) and containing the role ROLE_COMMUNITY.

The service offers the following endpoints:

'List<Offenders>    /communityapi/api/staff/staffCode/{staffCode}/managedOffenders'
'ResponsibleOfficer /communityapi/api/offenders/nomsNumber/{nomsId}/responsibleOfficer'
'/communityapi/health'
'/communityapi/status'

There is a small amount of translation done within the proxy to provide a clearer API for the client.

# Build, test, assemble the JAR and run locally

 $ ./gradlew clean test assemble bootRun

# Pipeline

There is a .circleci/config.yml file which defines the workflow steps.

# Curl Examples

Request:

`curl -X GET -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImF1dGhfc291cmNlIjoibm9uZSIsImV4cCI6MTg4MTg5MzM5MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9TWVNURU1fVVNFUiIsIlJPTEVfR0xPQkFMX1NFQVJDSCIsIlJPTEVfQ09NTVVOSVRZIiwiUk9MRV9MSUNFTkNFX1JPIl0sImp0aSI6ImMyNTdkYTMwLTUxNTgtNDM0Ni04NDNhLTU0NmE3ODA3ZjJiMiIsImNsaWVudF9pZCI6ImxpY2VuY2VzYWRtaW4ifQ.AV1qmGa8p5YkvVPCqNtHVEJ-Mse3J9CCdqYmtSz_VK8Mqdw26EJIczQSQRW3UFe5G78WST4u1GA9XQUKykxnh9dlAJpPs4p4YYEOT8MHIfmF7YCRKea-hZkU4FI_L2Rmjnfu1XOvA3LilMEWyl1QTkzjS22GLp7C9oWmfnk1pRrBiiG-kr5Q4S8jgfvje0GNBQQkFWJo7E3QlMHoH2EP9ufRgcEycNZ4qcmZ6vF_-ilcY-dDsCn9CspXPeAD8N3i7zkM-6h14T92xf0Is4AIigqNzHPBPJbDsEzz9dacgtFvepldpo_2VP2HMnDc2Zm7TLt0asgNItgR30fMPl8uFw" \
https://community-api-t2.hmpps.dsd.io/communityapi/api/offenders/nomsNumber/888/responsibleOfficers`

Response: 

`{"username":"JMJARRE1","staffCode":"AA999B","forenames":"Jean Michel","surname":"Jarre"}`

Request:

`curl -X GET -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImF1dGhfc291cmNlIjoibm9uZSIsImV4cCI6MTg4MTg5MzM5MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9TWVNURU1fVVNFUiIsIlJPTEVfR0xPQkFMX1NFQVJDSCIsIlJPTEVfQ09NTVVOSVRZIiwiUk9MRV9MSUNFTkNFX1JPIl0sImp0aSI6ImMyNTdkYTMwLTUxNTgtNDM0Ni04NDNhLTU0NmE3ODA3ZjJiMiIsImNsaWVudF9pZCI6ImxpY2VuY2VzYWRtaW4ifQ.AV1qmGa8p5YkvVPCqNtHVEJ-Mse3J9CCdqYmtSz_VK8Mqdw26EJIczQSQRW3UFe5G78WST4u1GA9XQUKykxnh9dlAJpPs4p4YYEOT8MHIfmF7YCRKea-hZkU4FI_L2Rmjnfu1XOvA3LilMEWyl1QTkzjS22GLp7C9oWmfnk1pRrBiiG-kr5Q4S8jgfvje0GNBQQkFWJo7E3QlMHoH2EP9ufRgcEycNZ4qcmZ6vF_-ilcY-dDsCn9CspXPeAD8N3i7zkM-6h14T92xf0Is4AIigqNzHPBPJbDsEzz9dacgtFvepldpo_2VP2HMnDc2Zm7TLt0asgNItgR30fMPl8uFw" \
https://community-api-t2.hmpps.dsd.io/communityapi/api/staff/staffCode/888/managedOffenders`

Response: 

` [{"offenderNo":"CT800X"},{"offenderNo":"CR811Y"}]`

Request:

`curl -X GET https://community-api-t2.hmpps.dsd.io/communityapi/health`

Response:

`{ "status":"UP"}`


# Properties

This following properties should be overriden by environment variables for non-local environments, provided as secrets :

`JWT_PUBLIC_KEY - the base64-encoded public key of the oauth signing server for this environment     
TRUST_STORE_PASSWORD - the password for the SSL trusted certificate store
DELIUS_ENDPOINT_URL - the base endpoint for the Delius API(e.g https://oasys400.noms.gsi.gov.uk)
DELIUS_USERNAME - the username to login to the Delius API and retrieve a token
DELIUS_NAME_IP_MAP - the IP address and FQDN for the Delius API host - for non-local environments`


# The Commmunity API

The Community API is deployed by Tolomy and resides in a private Uk Cloud network and infrastructure.

The following URLs are used to address it: 

    Stage (T2) - t2pml0007   - curl -v -k https://oasys400.noms.gsi.gov.uk/api/health
    Prod       - pdpml00025  - curl -v --resolve ndseis.ad.nps.internal:443:10.162.217.15 \
                                       --cacert ndseis-ad-nps-internal.crt https://ndseis.ad.nps.internal/api/health

# The Community Proxy 
    
The proxy application is deployed in a docker container on the NDH hosts :

`Stage       :   t2pml00007
Production  :   pdpml00025`
     
The community proxy service is temporary and will handle Oauth2 token authentication on behalf of the Community API until such time as that API
can be opened for more public access to authenticate requests itself.

# Stratey

The aim is to keep this proxy layer very thin - to do as little mapping / translation between client and Community API such that
when it moves to AWS the clients can consume directly with a small amount of refinement.


# Docker

The Dockerfile exposes port 8080 in the container and the docker run command maps this to 8081/tcp on the docker host.
 
To build & push the docker image to Docker Hub: 

` 
$ docker build -t mojdigitalstudio/community-proxy:latest .
$ docker login
$ docker push mojdigitalstudio/community-proxy:latest
`
 
To run the container locally, expose 8081 to the local host and resolve host names use :
  
`
$ docker pull mojdigitialstudio/community-proxy:latest
$ docker run -p 8081:8080 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest

To run in staging (on t2pml0007) :

$ docker run -p 8081:8080 --add-host=oasys400.noms.gsi.gov.uk:10.162.216.115 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest

In production:

$ docker run -p 8081:8080 --add-host=ndseis.ad.nps.internal:10.162.217.15 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest
`

# IntelliJ setup

- Install jdk 11
- Enable Gradle using jdk 11
- Set jdk11 in project structure
- Enable the lombok plugin in IntelliJ and restart if necessary
- Enable annotation Processing at "Settings > Build > Compiler > Annotation Processors"
- Ensure commandline and IntelliJ build project and pass all tests

