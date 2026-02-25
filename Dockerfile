FROM maven:3.9.12-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -Dmaven.test.skip=true


FROM eclipse-temurin:21-jre-alpine-3.23
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD [ "java", "-jar", "app.jar" ]
