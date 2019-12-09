# Community API proxy

We now go direct to community API via a /secure route, so we no longer need this proxy.

# Old README follows
This API accepts and authorises requests on behalf of the Community API and forwards only for authenticated clients.

The authentication process involves checking the JWT token provided by the client in the Authorization header, checking that the token
has the correct signature (ie. issued by the MOJ oauth2 service) and containing the role ROLE_COMMUNITY.

The service offers the following endpoints:

`List<ManagedOffender> GET /communityapi/api/staff/staffCode/{staffCode}/managedOffenders`

`List<ResponsibleOfficer> GET /communityapi/api/offenders/nomsNumber/{nomsId}/responsibleOfficers`

`String GET /communityapi/api/remote-status`


`String GET /communityapi/ping`: will respond `pong` to all requests.  This should be used by dependent systems to check connectivity to community proxy,
rather than calling the `/health` endpoint.

`String GET /communityapi/health`: provides information about the application health and its dependencies.  This should only be used
by community proxy health monitoring (e.g. pager duty) and not other systems who wish to find out the state of community proxy.

`String GET /communityapi/info`: provides information about the version of deployed application.


The full swagger documentation (in T2) can be found here:

`https://community-api-t2.hmpps.dsd.io/communityapi/swagger-ui.html`

There is a small amount of translation of requests within the proxy application but generally it is a very thin layer such that
when the Community API is migrated to a more public cloud platform the clients will be able to consume resources directly with 
only a very small amount of rework to these requests.

The only translation done is the addition of a query parameter `?current=true` to both endpoints when forwarding to the 
Community API.


# Overview Diagram

![Overview diagram](./HDC-to-Community-API.png)

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

`List<ManagedOffender>`

or 

`ErrorResponse`


Request (T2 only):

`curl -X GET -H "Authorization: bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImF1dGhfc291cmNlIjoibm9uZSIsImV4cCI6MTg4MTg5MzM5MSwiYXV0aG9yaXRpZXMiOlsiUk9MRV9TWVNURU1fVVNFUiIsIlJPTEVfR0xPQkFMX1NFQVJDSCIsIlJPTEVfQ09NTVVOSVRZIiwiUk9MRV9MSUNFTkNFX1JPIl0sImp0aSI6ImMyNTdkYTMwLTUxNTgtNDM0Ni04NDNhLTU0NmE3ODA3ZjJiMiIsImNsaWVudF9pZCI6ImxpY2VuY2VzYWRtaW4ifQ.AV1qmGa8p5YkvVPCqNtHVEJ-Mse3J9CCdqYmtSz_VK8Mqdw26EJIczQSQRW3UFe5G78WST4u1GA9XQUKykxnh9dlAJpPs4p4YYEOT8MHIfmF7YCRKea-hZkU4FI_L2Rmjnfu1XOvA3LilMEWyl1QTkzjS22GLp7C9oWmfnk1pRrBiiG-kr5Q4S8jgfvje0GNBQQkFWJo7E3QlMHoH2EP9ufRgcEycNZ4qcmZ6vF_-ilcY-dDsCn9CspXPeAD8N3i7zkM-6h14T92xf0Is4AIigqNzHPBPJbDsEzz9dacgtFvepldpo_2VP2HMnDc2Zm7TLt0asgNItgR30fMPl8uFw" \
https://community-api-t2.hmpps.dsd.io/communityapi/api/staff/staffCode/888/managedOffenders`

Response: 

`List<ResponsibleOfficer>`

or 

`ErrorResponse`

Request (T2 only):

`curl -X GET https://community-api-t2.hmpps.dsd.io/communityapi/health`

Response:

`{ "status":"UP"}`


`curl -X GET https://community-api-t2.hmpps.dsd.io/communityapi/api/remote-status`

Response:

`{ The full remote status info from the Community API incl. disk space, db status etc }`


# Properties to Override in non-local Environments

The base64-encoded public key of the oauth signing server for this environment:
 
`JWT_PUBLIC_KEY=X4H4H4H3h3...` 
 
 The password for the SSL trusted certificate store:
 
`TRUST_STORE_PASSWORD=secret` 

 The base endpoint for the Delius API (e.g https://oasys400.noms.gsi.gov.uk/api) :

`DELIUS_ENDPOINT_URL=https://host:port/api`

 The username to login to the Delius API and retrieve a token:

`DELIUS_API_USERNAME=DeliusAdminUserName`

 The FQDN and IP address for the Delius API host (used in the docker run command to add a host entry):

`DELIUS_NAME_IP_MAP=ndseis.ad.nps.internal:10.162.217.15`

 The application insights key to forward logging events to Azure

`APPLICATION_INSIGHTS_IKEY=abbe4433bb434b34bb...`

[ if omitted or an empty value it will not record log events to Azure application insights ] 

`SPRING_PROFILES_ACTIVE=production,logstash`

The default profile is production (yes, even in non-prod environments) and adding logstash will send logs to AI

# Trusted Certificates

The docker images is built to contain a trust store that is populated with the certificates
which will be trusted by the proxy for forward HTTPS connections. At present this contains the current 
X.509 certificates for two planned environments T2 and PRODUCTION.

When these certificates change the keystore will need to be updated either with the new host, intermediate 
or CA root certificates as advised by Tolomy.

There is a script in the ${project-root}/keystores directory for recreating this keystore and the 
Dockerfile will ensure it is included in the image.


# The Commmunity API

The Community API is deployed by Tolomy and resides in a private UK Cloud network and infrastructure.

The following URLs have been provided to us and accessible only from these source IPs / hosts : 

`From t2pml0007 (stage env) : $ curl -v -k https://oasys400.noms.gsi.gov.uk/api/health`
    
`From pdpml00025 (production) : $curl -v --resolve ndseis.ad.nps.internal:443:10.162.217.15 \
                                      --cacert ndseis-ad-nps-internal.crt https://ndseis.ad.nps.internal/api/health`
     

# Docker

The application listens on port 8080 within the container and the docker run command maps this to 8081/tcp on the host.
 
To build & push the docker image to Docker Hub: 

`$ docker build -t mojdigitalstudio/community-proxy:latest .`

`$ docker login`

`$ docker push mojdigitalstudio/community-proxy:latest`
 
To run the container locally and expose 8081 to the local host use :
  
`$ docker pull mojdigitialstudio/community-proxy:latest`

`$ docker run -p 8081:8080 --name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest`

[ This will use default properties rather then overriding them with environment variables ] 


To run in staging (t2pml0007) 

Environment: 

`
 TRUST_STORE_PASSWORD=<the password used when creating the trust store>
 JWT_PUBLIC_KEY=<from the oauth system>
 DELIUS_ENDPOINT_URL=https://oasys400.noms.gsi.gov.uk/api
 APPLICATION_INSIGHTS_IKEY=<from Azure portal for T2>
 DELIUS_API_USERNAME=<from Delius API team>
 DELIUS_NAME_IP_MAP=oasys400.noms.gsi.gov.uk:10.162.216.115
 SPRING_PROFILES_ACTIVE=production,logstash
`

`$ docker run -p 8081:8080 \
             --name "community-proxy" \
             --add-host=${DELIUS_NAME_IP_MAP} \
             -e TRUST_STORE_PASSWORD=xxxxxx \
             -e JWT_PUBLIC_KEY=xxxxxx \
             -e DELIUS_ENDPOINT_URL=https://oasys400.noms.gsi.gov.uk/api
             -e DELIUS_API_USERNAME=xxxxxxx \
             -e APPLICATION_INSIGHTS_IKEY=xxxxx \
             -e SPRING_PROFILES_ACTIVE=production,logstash \
             -d -t mojdigitalstudio/community-proxy:latest`

[ For T2 omit the JWT_PUBLIC_KEY to accept the default which is the T3 public key ] 


To run in production (pdpml00025) :

Environment:

`
 TRUST_STORE_PASSWORD=<the password used when creating the trust store>
 JWT_PUBLIC_KEY=<from the oauth system>
 DELIUS_ENDPOINT_URL=https://ndseis.ad.nps.internal/api
 APPLICATION_INSIGHTS_IKEY=<from Azure portal for production>
 DELIUS_API_USERNAME=<from Delius API team>
 DELIUS_NAME_IP_MAP=ndseis.ad.nps.internal:10.162.217.15
 SPRING_PROFILES_ACTIVE=production,logstash
`

`$ docker run -p 8081:8080 \
             --name "community-proxy" \
             --add-host=${DELIUS_NAME_IP_MAP} \
             -e TRUST_STORE_PASSWORD=xxxxxx \
             -e JWT_PUBLIC_KEY=xxxxxx \
             -e DELIUS_ENDPOINT_URL=https://ndseis.ad.nps.internal/api
             -e DELIUS_API_USERNAME=xxxxxxx \
             -e APPLICATION_INSIGHTS_IKEY=xxxxx \
             -e SPRING_PROFILES_ACTIVE=production,logstash \
             -d -t mojdigitalstudio/community-proxy:latest`

[ For PRODUCTION - supply all values - no defaults ]

To run in test (against Delius SR2 from Cloud Platform) :

Environment:

`<Defined in Kubernetes Cloud Platform>`

In this environment the proxy is deployed into a Probation owned Kubernetes namespace (court-probation-dev). This allows routing to a VPN/SSH enabled proxy (called community-api-proxy) which then routes traffic back in to UKCloud. This allows testing against the Delius SR2 environment though is likely to be deprecated own the test environment has been migrated to AWS as part of the Delius AWS migration project.

Given this routes internally with a namespace via plain HTTP (before routing via SSH) no specific certificate store setup is required.

# Run Locally (outside of Docker)

Supply the information required (detailed in the run.sh, which docker uses)

java -Djavax.net.ssl.trustStore=keystores/trusted.jks \
     -Djavax.net.ssl.trustStorePassword=<trust store passwd> \
     -Djavax.net.ssl.trustStoreType=jks \
     -Ddelius.endpoint.url=<delius endpoint URL> \
     -Ddelius.api.username=<delius username> \
     -Djwt.public.key=<supply or omit to use default T3> \
     -jar build/libs/community-proxy-2019-06-19.jar

(NOTE: To test whether events are generated to app insights, you can specify a property of -DAPPLICATION_INSIGHTS_IKEY=xxxxx
       or an envionment variable of APPLICATION_INSIGHTS_IKEY and SPRING_PROFILES_ACTIVE in logstash. Best not to pollute 
       real environments with local stats too much though).

# Maintenance tasks

* Check the SSL certificate expiry dates for the Tolomy gateways in T2 and Production 
* If expired the new certs (or preferable the root/intermediate CA certs) will need to be added to the trust store.
* Health monitoring will show whether the remote Community API is healthy or not
* Application insights will record logged events to show usage


# IntelliJ setup

- Install jdk 11
- Enable Gradle using jdk 11
- Set jdk11 in project structure
- Enable the lombok plugin in IntelliJ and restart
- Enable annotation Processing at "Settings > Build > Compiler > Annotation Processors"
- Ensure commandline and IntelliJ build project and pass all tests
