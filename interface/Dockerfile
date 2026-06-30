FROM eclipse-temurin:17-jre

WORKDIR /app

COPY frank-api-interface-*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
