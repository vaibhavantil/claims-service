FROM openjdk:10.0.2-13-jdk-slim-sid


ADD target/claims-service-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar claims-service-0.0.1-SNAPSHOT.jar
