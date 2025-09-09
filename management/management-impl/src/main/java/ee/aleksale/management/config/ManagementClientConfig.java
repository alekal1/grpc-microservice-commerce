package ee.aleksale.management.config;

import ee.aleksale.client.proto.v1.ClientRegistrationServiceGrpc;
import ee.aleksale.credentials.CommerceSecretCallCredentials;
import ee.aleksale.inventory.proto.v1.InventoryManagementServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
@RequiredArgsConstructor
public class ManagementClientConfig {

    @Bean
    public InventoryManagementServiceGrpc.InventoryManagementServiceStub inventoryServiceStub(
            GrpcChannelFactory channels,
            CommerceSecretCallCredentials secretCallCredentials) {
        return InventoryManagementServiceGrpc.newStub(channels.createChannel("inventory"))
                .withCallCredentials(secretCallCredentials);
    }

    @Bean
    public ClientRegistrationServiceGrpc.ClientRegistrationServiceBlockingStub clientManagementBlockingStub(
            GrpcChannelFactory channels,
            CommerceSecretCallCredentials secretCallCredentials) {
        return ClientRegistrationServiceGrpc.newBlockingStub(channels.createChannel("client"))
                .withCallCredentials(secretCallCredentials);
    }
}
