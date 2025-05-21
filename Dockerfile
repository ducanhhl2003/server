FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests


# Run stage

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/sach-1.0.war drcomputer.war
EXPOSE 8081

ENTRYPOINT ["java","-jar","drcomputer.war"]
