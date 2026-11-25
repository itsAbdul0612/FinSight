#FROM eclipse-temurin:21-jre
#
#WORKDIR /app
#
#COPY  ./target/finsight-0.0.1-SNAPSHOT.jar .
#
#EXPOSE 8080
#
#CMD ["java", "-jar", "finsight-0.0.1-SNAPSHOT.jar"]


# Stage:1 Build
FROM maven:3.9.4-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage:2 Run
FROM eclipse-temurin:21-jre-alphine
WORKDIR /app
COPY --from=builder /app/target/finsight-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "finsight-0.0.1-SNAPSHOT.jar"]
