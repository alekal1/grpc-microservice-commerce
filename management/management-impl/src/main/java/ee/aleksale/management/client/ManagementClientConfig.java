package ee.aleksale.management.client;

import ee.aleksale.client.proto.v1.ClientRegistrationServiceGrpc;
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
    public ClientRegistrationServiceGrpc.ClientRegistrationServiceBlockingStub clientManagementBlockingStub(GrpcChannelFactory channels) {
        return ClientRegistrationServiceGrpc.newBlockingStub(channels.createChannel("client"));
    }
}
