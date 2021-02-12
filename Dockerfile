
##### Dependencies stage #####
FROM maven:3.6.3-amazoncorretto-11 AS dependencies

# Resolve dependencies and cache them
COPY pom.xml /
RUN mvn dependency:go-offline


##### Build stage #####
FROM dependencies AS build

# Copy application source and build it
COPY src/main /src/main
COPY lombok.config /
RUN mvn clean package


##### Test stage #####
FROM dependencies AS test

# Copy test source and build+run tests
COPY src/test /src/test
COPY --from=build /target /target
RUN mvn test


##### Assemble artifact #####
FROM amazoncorretto:11-alpine AS assemble

# Fetch the datadog agent
RUN apk --no-cache add curl
RUN curl -o dd-java-agent.jar -L 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST'

# Install ffmpeg
RUN apk --no-cache add ffmpeg

# Copy the jar from build stage to this one
COPY --from=build /target/claims-service-0.0.1-SNAPSHOT.jar /

# Define entry point
ENTRYPOINT java -javaagent:/dd-java-agent.jar -jar claims-service-0.0.1-SNAPSHOT.jar
