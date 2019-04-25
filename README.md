# Community API proxy

This API accepts and authorises requests on behalf of the Commnunity API and only forwards authenticated clients.

The authentication process involves checking the JWT token provided by the client as a header, checking that the token
has the correct signature (ie. issued by the MOJ oauth2 service) and that the token contains the role that is appropriate for 
the resource requested, in this case ROLE_COMMUNITY_API.

The service offers the following endpoints:

'''List<Offenders>   /communityapi/api/offenderManagers/staffCode/{staffId}/offenders   '''
'''ResponsibleOfficer /communityapi/api/offenders/nomsNumber/{nomsId}/responsibleOfficer'''
'''/communityapi/health'''
'''/communityapi/status'''

These are identical (apart from the leading "/communityapi/") to what is accepted by the real CommunityApi.

# Build, test and run locally

 $ ./gradlew clean test assemble bootRun

# Pipeline

There is a .circleci/config.yml file which defines the workflow steps.

# Properties

server.port             :    8080 within the container
community.endpoint.url  :    The URL where the community API listens e.g. https://ndseis.ad.nps.internal | oasys400.noms.gsi.gov.uk


# Deployment

Deployment is handled manually. This sevice resides in the Fix And Go environment:

    Stage (T2) - t2pml0007   - curl -v -k https://oasys400.noms.gsi.gov.uk/api/health
    Prod       - pdpml00025  - curl -v --resolve ndseis.ad.nps.internal:443:10.162.217.15 \
                                       --cacert ndseis-ad-nps-internal.crt https://ndseis.ad.nps.internal/api/health
    
The proxy application is deployed in a docker container on the NDH hosts above and acts as a proxy service for the Community API.

The service is temporary and will handle Oauth2 token authentication on behalf of the Community API until such time it can be altered 
to perform this itself

# Docker

The Dockerfile exposes port 8080 in the container and the docker run command maps this to 8081/tcp on the docker host.
 
To build & push the docker image to Docker Hub: 
 
$ docker build -t mojdigitalstudio/community-proxy:latest .
$ docker login
$ docker push mojdigitalstudio/community-proxy:latest
 
To run the container locally and expose 8081 to the local host:
  
$ docker pull mojdigitialstudio/community-proxy:latest
$ docker run -p 8081:8080 -name "community-proxy" -d -t mojdigitalstudio/community-proxy:latest


# IntelliJ setup

- Install jdk 11
- Enable Gradle using jdk 11
- Set jdk11 in project structure
- Enable the lombok plugin in IntelliJ and restart if necessary
- Enable annotation Processing at "Settings > Build > Compiler > Annotation Processors"
- Ensure commandline and IntelliJ build project and pass all tests

