# Fake maven's dependencies stage so we can use the same pipelne.
FROM scratch as dependencies

##### Copy files and build #####
FROM gradle:6.8.2-jdk11 AS build
WORKDIR /claims-service

# set gradle cache
RUN mkdir -p /gradle-cache
ENV GRADLE_USER_HOME /home/gradle

# copy files
COPY build.gradle .
COPY settings.gradle .
COPY lombok.config .
COPY src/main src/main

# build app
RUN gradle --no-daemon build --stacktrace

##### Run tests #####
FROM build AS test
COPY src/test src/test
RUN gradle --no-daemon test --stacktrace

##### Build jar #####
FROM build AS bootjar
RUN gradle --no-daemon bootjar --stacktrace

##### Assemble artifact #####
FROM amazoncorretto:11-alpine AS assemble
WORKDIR /

# Fetch the datadog agent
RUN apk --no-cache add curl
RUN curl -o dd-java-agent.jar -L 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST'

# Install ffmpeg
RUN apk --no-cache add ffmpeg

# Copy the jar from build stage to this one
COPY --from=bootjar claims-service/build/libs/claims-service-0.0.1-SNAPSHOT.jar /app/target/claims-service-0.0.1-SNAPSHOT.jar

# Define entry point
ENTRYPOINT java -javaagent:/dd-java-agent.jar -jar /app/target/claims-service-0.0.1-SNAPSHOT.jar
