FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew :adaptive-education-service:bootJar --no-daemon --no-configuration-cache

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/adaptive-education-service/build/libs/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]