#docker build --build-arg=target/socatel-0.0.1-SNAPSHOT.jar -f src/main/docker/Dockerfile . -t socatel-platform
FROM openjdk:8-jdk-alpine
#VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=socatel-gcp.jar
COPY ${JAR_FILE} socatel.jar
#ADD keystore.p12 keystore.p12
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/socatel.jar"]