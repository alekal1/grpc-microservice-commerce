FROM openjdk:17-jdk-slim
COPY payment-impl/target/payment-impl-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 9094