FROM openjdk:11-slim
MAINTAINER HMPPS Digital Studio <info@digital.justice.gov.uk>

RUN apt-get update && apt-get install -y curl

WORKDIR /app

COPY build/libs/community-proxy*.jar /app/app.jar
COPY run.sh /app
COPY src/main/resources/*.crt /app/

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "/app/run.sh"]
