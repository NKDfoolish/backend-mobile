![Mô tả ảnh](https://repository-images.githubusercontent.com/149085612/76e31b00-782c-11e9-8fdc-eec6e93d055b)

# 1. Prerequisite

Cài đặt JDK 17+ nếu chưa thì cài đặt JDK

Install Maven 3.5+ nếu chưa thì cài đặt Maven

Install IntelliJ nếu chưa thì cài đặt IntelliJ

Install Docker nếu chưa thì cài đặt Docker

# 2. Technical Stacks

Java 17

Maven 3.5+

Spring Boot 3.3.4

Spring Data Validation

Spring Data JPA

Postgres/MySQL (optional)

Lombok

DevTools

Docker

Docker compose

…


# 3. Build & Run Application

– Run application bởi mvnw tại folder backend-service

```bash
  $ ./mvnw spring-boot:run
```

– Run application bởi docker
```bash
  $ mvn clean install -P dev
  $ docker build -t backend-service:latest .
  $ docker run -it -p 8080:8080 --name backend-service backend-service:latest
```



# 4. Test

– Check health với cURL

```bash
  curl --location 'http://localhost:8080/actuator/health'
```

-- Response --

{
    "status": "UP"
}

– Truy cập Backend service để test các API
