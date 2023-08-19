FROM eclipse-temurin:17-jre-focal

# Metadata as described above
LABEL maintainer="tony.wu122@imperial.ac.uk"
LABEL version="1.0"
LABEL description="Docker image for the BLOCC Dashboard Spring Boot backend"

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the image
COPY build/libs/blocc-dashboard-backend-*-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]