#FROM maven:3.8.5-openjdk-17 AS build
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM openjdk:17.0.1-jdk-slim
#COPY --from=build /target/shoe-fast-be-0.0.1-SNAPSHOT.jar shoe-be.jar
#EXPOSE 8086
#ENTRYPOINT ["java", "-jar", "shoe-be.jar"]
#

# Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests

# Run stage
FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/shoe-fast-be-0.0.1-SNAPSHOT.jar shoe-be.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "shoe-be.jar"]

