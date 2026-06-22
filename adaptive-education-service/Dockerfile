FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .
RUN ./gradlew :adaptive-education-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/adaptive-education-service/build/libs/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
