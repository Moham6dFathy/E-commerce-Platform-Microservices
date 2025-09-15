# Use official OpenJDK 21 runtime as a parent image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory inside the container
WORKDIR /app

# Copy the built jar file from target
COPY target/*.jar app.jar

# Expose port (make sure it matches your application.properties/server.port)
EXPOSE 9000

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
