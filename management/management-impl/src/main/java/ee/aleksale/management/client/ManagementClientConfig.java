package ee.aleksale.management.client;

import ee.aleksale.client.proto.v1.ClientRegistrationServiceGrpc;
import ee.aleksale.inventory.proto.v1.InventoryServiceGrpc;
import ee.aleksale.management.credentials.SecretCallCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class ManagementClientConfig {

    @Bean
    public InventoryServiceGrpc.InventoryServiceStub inventoryServiceStub(
            GrpcChannelFactory channels,
            SecretCallCredentials secretCallCredentials) {
        return InventoryServiceGrpc.newStub(channels.createChannel("inventory"))
                .withCallCredentials(secretCallCredentials);
    }

    @Bean
    public ClientRegistrationServiceGrpc.ClientRegistrationServiceBlockingStub clientManagementBlockingStub(
            GrpcChannelFactory channels,
            SecretCallCredentials secretCallCredentials) {
        return ClientRegistrationServiceGrpc.newBlockingStub(channels.createChannel("client"))
                .withCallCredentials(secretCallCredentials);
    }
}
