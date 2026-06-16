FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S ilp && adduser -S ilp -G ilp

COPY adaptive-education-service/build/libs/*.jar app.jar

ENV SERVER_PORT=8083
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/msf
ENV SPRING_DATASOURCE_USERNAME=msf
ENV SPRING_DATASOURCE_PASSWORD=msf
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

EXPOSE 8083

USER ilp

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
