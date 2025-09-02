FROM openjdk:17-jdk-slim
COPY client-impl/target/client-impl-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 9092