FROM openjdk:17-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot app JAR file to the container
COPY target/foresty-app-0.0.1-SNAPSHOT.jar /app/my-app.jar

# Set the command to run the Spring Boot app when the container starts
CMD ["java", "-jar", "/app/my-app.jar"]
