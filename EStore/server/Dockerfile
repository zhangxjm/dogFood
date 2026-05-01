FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/estore-server-1.0.0.jar app.jar

ENV TZ=Asia/Shanghai

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
