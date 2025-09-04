package ee.aleksale.client.service.grpc;

import com.google.protobuf.Any;
import ee.aleksale.client.interceptors.IdentificationHeaderInterceptor;
import ee.aleksale.client.proto.v1.ClientServiceGrpc;
import ee.aleksale.client.service.ClientService;
import ee.aleksale.common.client.proto.v1.Client;
import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.response.proto.v1.ErrorResponse;
import ee.aleksale.common.response.proto.v1.SuccessResponse;
import ee.aleksale.order.proto.v1.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService(interceptors = IdentificationHeaderInterceptor.class)
@RequiredArgsConstructor
public class ClientGrpcService extends ClientServiceGrpc.ClientServiceImplBase {

    private final ClientService clientService;
    private final OrderServiceGrpc.OrderServiceStub orderServiceStub;

    @Override
    public void addMoney(Client request, StreamObserver<CommerceResponse> responseObserver) {

        try {
            var client = clientService.addMoney(IdentificationHeaderInterceptor.ID_CONTEXT_KEY.get(), request.getMoney());

            responseObserver.onNext(CommerceResponse.newBuilder()
                    .setSuccess(SuccessResponse.newBuilder()
                            .addData(Any.pack(client))
                            .setMessage("Money added.")
                            .build())
                    .build());
        } catch (Exception e) {
            responseObserver.onNext(CommerceResponse.newBuilder()
                    .setError(ErrorResponse.newBuilder()
                            .setCode(500)
                            .setMessage(e.getMessage())
                            .build())
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<InventoryOrder> makeOrder(StreamObserver<CommerceResponse> responseObserver) {
        return orderServiceStub.makeOrder(responseObserver);
    }
}
