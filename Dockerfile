# Use the official OpenJDK image from the Docker Hub
FROM openjdk:20-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the entire project directory to the container
COPY . /app

# Check if the file is copied correctly
RUN ls -la /app

# Create a directory for logs
RUN mkdir -p /var/log/myapp

# Set an environment variable for the log file path
ENV LOG_FILE_PATH=/var/log/myapp/app.log

# Expose the port the application runs on
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/target/MarkPriceController-0.0.1-SNAPSHOT.jar", "--debug"]