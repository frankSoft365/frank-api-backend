FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

ARG GITHUB_TOKEN

COPY pom.xml .
COPY src ./src

RUN mkdir -p /root/.m2 && \
    echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \
    <settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"> \
        <servers> \
            <server> \
                <id>github</id> \
                <username>frankSoft365</username> \
                <password>${GITHUB_TOKEN}</password> \
            </server> \
        </servers> \
    </settings>" > /root/.m2/settings.xml

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/frank-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]