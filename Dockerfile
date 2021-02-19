##### Copy files #####
FROM gradle:6.8.2-jdk11 AS build
WORKDIR /claims-service/

COPY src/ src/
COPY build.gradle .
COPY settings.gradle .
COPY lombok.config .

RUN gradle build

##### Run tests #####
FROM build AS test
RUN gradle test

##### Build jar #####
FROM build AS jar
RUN gradle bootjar

##### Assemble artifact #####
FROM amazoncorretto:11-alpine AS assemble
WORKDIR /

# Fetch the datadog agent
RUN apk --no-cache add curl
RUN curl -o dd-java-agent.jar -L 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST'

# Install ffmpeg
RUN apk --no-cache add ffmpeg

# Copy the jar from build stage to this one
COPY --from=jar claims-service/build/libs/claims-service-0.0.1-SNAPSHOT.jar /app/target/claims-service-0.0.1-SNAPSHOT.jar

# Define entry point
ENTRYPOINT java -javaagent:/dd-java-agent.jar -jar /app/target/claims-service-0.0.1-SNAPSHOT.jar
