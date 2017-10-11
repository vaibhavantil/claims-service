FROM openjdk:8


ADD target/claims-service-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar claims-service-0.0.1-SNAPSHOT.jar
