FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/flowable-workflow-1.0.0.jar app.jar

# Expose the port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]