##### Dependencies stage #####
FROM amazoncorretto:11 AS gradle_setup
WORKDIR /usr/app
ENV GRADLE_USER_HOME=/usr/share/gradle/
COPY gradlew .
COPY gradle gradle
RUN ./gradlew --version

FROM gradle_setup AS dependencies
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY lombok.config .

# running 'build' with only a gradle-file will only fetch dependencies
# we explicitly omit bootJar, since it requires a main class file, which we don't have at this stage
RUN ./gradlew clean build -x bootJar --no-daemon


##### Build stage #####
FROM dependencies AS build

# Copy application source and build it
COPY src/main src/main
RUN ./gradlew bootJar --no-daemon


##### Test stage #####
FROM build AS test
COPY src/test src/test
RUN ./gradlew test --no-daemon


##### Assemble stage #####
FROM amazoncorretto:11-alpine AS assemble

# Fetch the datadog agent
RUN apk --no-cache add curl
RUN curl -o dd-java-agent.jar -L 'https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.datadoghq&a=dd-java-agent&v=LATEST'

# Install ffmpeg
RUN apk --no-cache add ffmpeg

# Copy the jar from build stage to this one
COPY --from=build /usr/app/build/libs/claims-service-0.0.1-SNAPSHOT.jar /app/target/claims-service-0.0.1-SNAPSHOT.jar

# Define entry point
ENTRYPOINT java -javaagent:/dd-java-agent.jar -jar /app/target/claims-service-0.0.1-SNAPSHOT.jar
