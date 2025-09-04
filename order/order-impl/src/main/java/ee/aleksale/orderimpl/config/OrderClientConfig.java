package ee.aleksale.orderimpl.config;

import ee.aleksale.inventory.proto.v1.InventoryAvailabilityServiceGrpc;
import ee.aleksale.payment.proto.v1.PaymentServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class OrderClientConfig {

    @Bean
    PaymentServiceGrpc.PaymentServiceStub paymentServiceBlockingStub(GrpcChannelFactory channels) {
        return PaymentServiceGrpc.newStub(channels.createChannel("payment"));
    }

    @Bean
    InventoryAvailabilityServiceGrpc.InventoryAvailabilityServiceBlockingStub inventoryAvailabilityServiceBlockingStub(
            GrpcChannelFactory channels) {
        return InventoryAvailabilityServiceGrpc.newBlockingStub(channels.createChannel("inventory"));
    }
}
