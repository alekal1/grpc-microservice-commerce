package ee.aleksale.client.service.grpc;

import ee.aleksale.client.service.ClientService;
import ee.aleksale.common.client.proto.v1.Client;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ClientManagementGrpcServiceTest {

    private ClientService clientService;

    private ClientManagementGrpcService clientManagementGrpcService;

    @BeforeEach
    void init() {
        clientService = mock(ClientService.class);

        clientManagementGrpcService = new ClientManagementGrpcService(
                clientService
        );
    }


    @Test
    void registerClient_negativeMoney() {
        var client = Client.newBuilder()
                .setMoney(-1.0)
                .build();

        var responseObserver = StreamRecorder.<CommerceResponse>create();

        clientManagementGrpcService.registerClient(client, responseObserver);

        assertEquals(1, responseObserver.getValues().size());
        var commerceResponse = responseObserver.getValues().get(0);

        assertTrue(commerceResponse.hasError());

        var error = commerceResponse.getError();
        assertNotNull(error.getMessage());
    }

    @Test
    void registerClient() {
        var savedClient = Client.getDefaultInstance();
        var request = Client.newBuilder()
                .setMoney(10.0)
                .build();
        var responseObserver = StreamRecorder.<CommerceResponse>create();

        doReturn(savedClient)
                .when(clientService)
                .saveClient(request);

        clientManagementGrpcService.registerClient(request, responseObserver);

        assertEquals(1, responseObserver.getValues().size());
        var commerceResponse = responseObserver.getValues().get(0);

        assertTrue(commerceResponse.hasSuccess());

        var success = commerceResponse.getSuccess();
        assertNotNull(success.getMessage());

    }
}
