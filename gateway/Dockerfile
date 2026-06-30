FROM eclipse-temurin:17-jre

WORKDIR /app

COPY frank-api-gateway-*.jar app.jar

EXPOSE 8123

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
