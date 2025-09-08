package ee.aleksale.client.service.grpc;

import ee.aleksale.client.exception.ClientException;
import ee.aleksale.client.interceptors.IdentificationHeaderInterceptor;
import ee.aleksale.client.service.ClientService;
import ee.aleksale.common.client.proto.v1.Client;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.order.proto.v1.OrderServiceGrpc;
import io.grpc.Context;
import io.grpc.internal.testing.StreamRecorder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientGrpcServiceTest {

    private ClientService clientService;
    private OrderServiceGrpc.OrderServiceStub orderServiceStub;

    private ClientGrpcService clientGrpcService;

    @BeforeEach
    void init() {
        clientService = mock(ClientService.class);
        orderServiceStub = mock(OrderServiceGrpc.OrderServiceStub.class);

        clientGrpcService = new ClientGrpcService(
                clientService,
                orderServiceStub
        );
    }

    @Test
    void addMoney_error() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var request = Client.getDefaultInstance();
        var errorMessage = "error";
        var idContextKey = UUID.randomUUID().toString();

        try (var ctx = Context.current()
                     .withValue(IdentificationHeaderInterceptor.ID_CONTEXT_KEY, idContextKey)
                     .withCancellation()) {
            ctx.attach();

            doThrow(new ClientException(errorMessage))
                    .when(clientService)
                    .addMoney(idContextKey, request.getMoney());

            clientGrpcService.addMoney(request, responseObserver);

            var values = responseObserver.getValues();
            assertEquals(1, values.size());

            var response = values.get(0);

            assertTrue(response.hasError());
            assertEquals(errorMessage, response.getError().getMessage());
        }
    }

    @Test
    void addMoney() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var request = Client.getDefaultInstance();
        var idContextKey = UUID.randomUUID().toString();

        try (var ctx = Context.current()
                .withValue(IdentificationHeaderInterceptor.ID_CONTEXT_KEY, idContextKey)
                .withCancellation()) {
            ctx.attach();

            doReturn(request)
                    .when(clientService)
                    .addMoney(idContextKey, request.getMoney());

            clientGrpcService.addMoney(request, responseObserver);

            var values = responseObserver.getValues();
            assertEquals(1, values.size());

            var response = values.get(0);

            assertTrue(response.hasSuccess());
        }
    }

    @Test
    void makeOrder_delegate() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();

        var expectedRequestObserver = mock(StreamObserver.class);

        doReturn(expectedRequestObserver)
                .when(orderServiceStub)
                .makeOrder(responseObserver);

        var actualRequestObserver = clientGrpcService.makeOrder(responseObserver);

        assertEquals(expectedRequestObserver, actualRequestObserver);
        verify(orderServiceStub).makeOrder(responseObserver);
    }
}
