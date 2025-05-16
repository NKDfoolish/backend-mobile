FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} backend-service.jar

# Kiểm tra nếu .env tồn tại thì copy, nếu không thì bỏ qua
RUN test -f .env && cp .env /app/.env || echo ".env not found"

ENTRYPOINT ["java", "-jar", "backend-service.jar"]

EXPOSE 8088
