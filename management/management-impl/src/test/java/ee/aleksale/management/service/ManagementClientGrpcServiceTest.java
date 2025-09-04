package ee.aleksale.management.service;

import ee.aleksale.client.proto.v1.ClientRegistrationServiceGrpc;
import ee.aleksale.common.client.proto.v1.Client;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ManagementClientGrpcServiceTest {

    private ClientRegistrationServiceGrpc.ClientRegistrationServiceBlockingStub clientRegistrationBlockingStub;
    private ManagementClientGrpcService managementClientGrpcService;

    @BeforeEach
    void init() {
        clientRegistrationBlockingStub = mock(ClientRegistrationServiceGrpc.ClientRegistrationServiceBlockingStub.class);

        managementClientGrpcService = new ManagementClientGrpcService(clientRegistrationBlockingStub);
    }

    @Test
    void registerClient() {
        var request = Client.getDefaultInstance();
        var response = CommerceResponse.getDefaultInstance();

        doReturn(response)
                .when(clientRegistrationBlockingStub)
                .registerClient(request);

        var responseObserver = StreamRecorder.<CommerceResponse>create();
        managementClientGrpcService.registerClient(request, responseObserver);

        assertNull(responseObserver.getError());
        var result = responseObserver.getValues();

        assertFalse(result.isEmpty());
        assertEquals(response, result.get(0));
    }
}
