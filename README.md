# Community API proxy

This API accepts and authorises requests on behalf of the Commnunity API and only forwards authenticated clients.

The authentication process involves checking the JWT token provided by the client as a header, checking that the token
has the correct signature (ie. issued by the MOJ oauth2 service) and that the token contains the role that is appropriate for 
the resource requested, in this case ROLE_COMMUNITY_API.

The service offers the following endpoints:

'''List<Offenders>   /communityapi/api/offenderManagers/staffCode/{staffId}/offenders   '''
'''ResponsibleOfficer /communityapi/api/offenders/nomsNumber/{nomsId}/responsibleOfficer'''

These are identical (apart from the leading "/communityapi/") to what is accepted by the real CommunityApi.

# Build, test and run locally

 $ ./gradlew clean test assemble bootRun

# Pipeline

There is a .circleci/config.yml file which defines the workflow steps.

# Environments

* T2 - Stage
* prod - Production

# Properties

server.port                             :     8080
community.endpoint.url      :    The URL where this service listens - http://host:port/communityapi 
community.api.uri.root        :    ${community.endpoint.url}/api


# Deployment

Deployment is handled manually. This sevice resides in the Fix And Go environment.
This is deployed to two NDH hosts to act as the Community API proxy.
The service is temporary and will handle Oauth2 token authentication on behalf of the CommunityAPI until such time as it performs this itself.

# Docker

To build the docker image: 

$ docker build -t ministryofjustice/community-proxy .
$ docker login
$ docker push ministryofjustice/community-proxy:latest 
$ docker run --detach -p 8080:8081 ministryofjustice/community-proxy


# IntelliJ setup

- Install jdk 11
- Enable Gradle using jdk 11
- Set jdk11 in project structure
- Ensure commandline and IntelliJ build project and pass all tests
- Enable the lombok plugin in IntelliJ and restart if necessary
- Enable annotation Processing at "Settings > Build > Compiler > Annotation Processors"
