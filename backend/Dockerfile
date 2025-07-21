# -------- Stage 1: Extract layered JAR contents --------
FROM bellsoft/liberica-openjre-debian:24-cds AS builder
WORKDIR /builder

# JAR built by Gradle (adjust filename if renamed)
ARG JAR_FILE=build/libs/project-management-app.jar
COPY ${JAR_FILE} application.jar

# Extract JAR into layers using Spring Boot's jarmode
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted


# -------- Stage 2: Final runtime image --------
FROM bellsoft/liberica-openjre-debian:24-cds
WORKDIR /application

# Copy layered contents
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/application/ ./

# Default to prod profile unless overridden
ENV SPRING_PROFILES_ACTIVE=prod

# Start app
ENTRYPOINT ["java", "-jar", "application.jar"]
