FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/classes/application.yml application.yml
RUN mkdir -p /uploads
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
