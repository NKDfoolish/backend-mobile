FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} backend-service.jar

COPY .env .env

ENTRYPOINT ["java", "-jar", "backend-service.jar"]

EXPOSE 8080