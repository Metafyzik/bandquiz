# 1. Use an official OpenJDK image as the base
FROM eclipse-temurin:21-jdk AS build

# 2. Set working directory
WORKDIR /app

# 3. Copy the Maven/Gradle wrapper and pom/build files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# 4. Download dependencies (to use Docker caching)
RUN ./mvnw dependency:go-offline

# 5. Copy the source code
COPY src ./src

# 6. Build the Spring Boot JAR
RUN ./mvnw clean package -DskipTests

# 7. Use a smaller JDK for running the app
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# 8. Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# 9. Run the app
ENTRYPOINT ["java","-jar","app.jar"]
