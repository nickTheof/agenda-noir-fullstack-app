# -------- Stage 0: Build JAR --------
FROM gradle:8-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# -------- Stage 1: Extract layered JAR contents --------
FROM bellsoft/liberica-openjre-debian:24-cds AS extractor
WORKDIR /builder
ARG JAR_FILE=build/libs/project-management-app.jar
COPY --from=builder /app/${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# -------- Stage 2: Final runtime image --------
FROM bellsoft/liberica-openjre-debian:24-cds
WORKDIR /application
COPY --from=extractor /builder/extracted/dependencies/ ./
COPY --from=extractor /builder/extracted/snapshot-dependencies/ ./
COPY --from=extractor /builder/extracted/spring-boot-loader/ ./
COPY --from=extractor /builder/extracted/application/ ./

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "application.jar"]
