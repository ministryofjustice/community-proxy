FROM openjdk:11-slim
MAINTAINER HMPPS Digital Studio <info@digital.justice.gov.uk>

RUN apt-get update && apt-get install -y curl

WORKDIR /app

COPY build/libs/community-proxy*.jar /app/app.jar
COPY run.sh /app

EXPOSE 8080

# Add system properties if using an external trust store.
# Also copy this trust store into the image.

ENTRYPOINT ["/bin/sh", "/app/run.sh"]
