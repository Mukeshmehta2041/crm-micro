FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/users-service-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"] 