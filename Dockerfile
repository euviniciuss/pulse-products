FROM maven:3.10-eclipse-temurin-27 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B package -DskipTests


FROM eclipse-temurin:27-jre

WORKDIR /app

RUN useradd --system --uid 1001 spring

COPY --from=build /workspace/target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]