FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY Catalog/pom.xml Catalog/pom.xml
COPY Order/pom.xml Order/pom.xml
COPY Users/pom.xml Users/pom.xml
COPY Supports/pom.xml Supports/pom.xml
COPY Payments/pom.xml Payments/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY api-gateway api-gateway
RUN mvn clean package -DskipTests -pl api-gateway -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
