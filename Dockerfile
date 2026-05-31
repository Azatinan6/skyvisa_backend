# 1. Aşama: Maven ve Java 21 ile projeyi derle
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# 2. Aşama: Sadece derlenen JAR dosyasını al ve çalıştır
FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]