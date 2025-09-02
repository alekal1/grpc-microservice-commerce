FROM openjdk:17-jdk-slim
COPY management-impl/target/management-impl-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 9090