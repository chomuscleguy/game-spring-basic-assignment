FROM eclipse-temurin:21-jdk-alpine

LABEL authors="chomu"

WORKDIR /app

COPY build/libs/*.jar /app/myapp.jar

ENTRYPOINT ["java","-jar", "/app/myapp.jar"]