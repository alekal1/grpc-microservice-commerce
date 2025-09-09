package ee.aleksale.client.service.grpc;

import com.google.protobuf.Any;
import ee.aleksale.client.interceptors.ManagementSecretInterceptor;
import ee.aleksale.client.proto.v1.ClientRegistrationServiceGrpc;
import ee.aleksale.client.service.ClientService;
import ee.aleksale.common.client.proto.v1.Client;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.response.proto.v1.ErrorResponse;
import ee.aleksale.common.response.proto.v1.SuccessResponse;
import ee.aleksale.credentials.CommerceSecretCallInterceptor;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService(interceptors = CommerceSecretCallInterceptor.class)
@RequiredArgsConstructor
public class ClientManagementGrpcService extends ClientRegistrationServiceGrpc.ClientRegistrationServiceImplBase {

    private final ClientService clientService;

    @Override
    public void registerClient(Client request, StreamObserver<CommerceResponse> responseObserver) {
        log.info("registerClient - onNext");

        if (request.getMoney() < 0) {
            responseObserver.onNext(CommerceResponse.newBuilder()
                    .setError(ErrorResponse.newBuilder()
                            .setCode(400)
                            .setMessage("Money cannot be less than zero")
                            .build())
                    .build());
            responseObserver.onCompleted();
            return;
        }

        var savedClient = clientService.saveClient(request);

        responseObserver.onNext(CommerceResponse.newBuilder()
                .setSuccess(SuccessResponse.newBuilder()
                        .addData(Any.pack(savedClient))
                        .setMessage("Registered! Do not forget your identifierCode!")
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
