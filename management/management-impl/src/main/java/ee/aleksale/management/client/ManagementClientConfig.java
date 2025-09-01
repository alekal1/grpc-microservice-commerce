package ee.aleksale.management.client;

import ee.aleksale.client.proto.v1.ClientServiceGrpc;
import ee.aleksale.inventory.proto.v1.InventoryServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class ManagementClientConfig {

    @Bean
    public InventoryServiceGrpc.InventoryServiceStub inventoryServiceStub(GrpcChannelFactory channels) {
        return InventoryServiceGrpc.newStub(channels.createChannel("inventory"));
    }

    @Bean
    public ClientServiceGrpc.ClientServiceBlockingStub clientServiceBlockingStub(GrpcChannelFactory channels) {
        return ClientServiceGrpc.newBlockingStub(channels.createChannel("client"));
    }
}
