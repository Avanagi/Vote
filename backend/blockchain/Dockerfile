FROM eclipse-temurin:24-jdk as builder

LABEL maintainer="Gorelov"

COPY target/*.jar app.jar

EXPOSE 8079

ENTRYPOINT ["java", "-jar", "app.jar"]
