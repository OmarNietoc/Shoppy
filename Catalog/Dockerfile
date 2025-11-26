FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY Catalog/pom.xml Catalog/pom.xml
COPY Order/pom.xml Order/pom.xml
COPY Users/pom.xml Users/pom.xml
COPY Supports/pom.xml Supports/pom.xml
COPY Payments/pom.xml Payments/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY Catalog/src Catalog/src
# We need to build the parent first or just build the specific module if possible.
# Since it's a multi-module project, it's safer to copy everything needed or build from root.
# However, to keep it simple per service, we can try to build just the module if parent is available.
# Let's copy the parent pom and the specific module.
COPY pom.xml .
COPY Catalog Catalog
RUN mvn clean package -DskipTests -pl Catalog -am

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/Catalog/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
