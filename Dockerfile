FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

USER 10001

ENTRYPOINT ["java", "-jar", "app.jar"]

