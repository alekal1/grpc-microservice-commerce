package ee.aleksale.orderimpl.config;

import ee.aleksale.credentials.CommerceSecretCallCredentials;
import ee.aleksale.inventory.proto.v1.InventoryAvailabilityServiceGrpc;
import ee.aleksale.payment.proto.v1.PaymentServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class OrderClientConfig {

    @Bean
    PaymentServiceGrpc.PaymentServiceFutureStub paymentServiceFutureStub(
            GrpcChannelFactory channels,
            CommerceSecretCallCredentials secretCallCredentials) {
        return PaymentServiceGrpc.newFutureStub(channels.createChannel("payment"))
                .withCallCredentials(secretCallCredentials);
    }

    @Bean
    InventoryAvailabilityServiceGrpc.InventoryAvailabilityServiceBlockingStub inventoryAvailabilityServiceBlockingStub(
            GrpcChannelFactory channels,
            CommerceSecretCallCredentials secretCallCredentials) {
        return InventoryAvailabilityServiceGrpc.newBlockingStub(channels.createChannel("inventory"))
                .withCallCredentials(secretCallCredentials);
    }
}
