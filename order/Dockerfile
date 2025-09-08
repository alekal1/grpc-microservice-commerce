FROM openjdk:17-jdk-slim
COPY order-impl/target/order-impl-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 9093