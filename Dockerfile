FROM adoptopenjdk/openjdk11:alpine-slim

ADD target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
