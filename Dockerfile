FROM openjdk:8


ADD target/asset-tracker-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar asset-tracker-0.0.1-SNAPSHOT.jar
