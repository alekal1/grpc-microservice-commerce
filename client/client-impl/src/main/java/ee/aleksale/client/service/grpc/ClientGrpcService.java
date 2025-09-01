package ee.aleksale.client.service.grpc;

import com.google.protobuf.Any;
import ee.aleksale.client.proto.v1.ClientServiceGrpc;
import ee.aleksale.client.service.ClientService;
import ee.aleksale.common.proto.v1.Client;
import ee.aleksale.common.proto.v1.CommerceResponse;
import ee.aleksale.common.proto.v1.SuccessResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ClientGrpcService extends ClientServiceGrpc.ClientServiceImplBase {

    private final ClientService clientService;

    @Override
    public void registerClient(Client request, StreamObserver<CommerceResponse> responseObserver) {
        log.info("registerClient - onNext");

        var response = clientService.saveClient(request);

        responseObserver.onNext(CommerceResponse.newBuilder()
                .setSuccess(SuccessResponse.newBuilder()
                        .addData(Any.pack(response))
                        .setMessage("Registered! Do not forget your identifierCode!")
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
