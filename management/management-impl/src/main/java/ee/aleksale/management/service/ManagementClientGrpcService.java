package ee.aleksale.management.service;

import ee.aleksale.client.proto.v1.ClientServiceGrpc;
import ee.aleksale.common.proto.v1.Client;
import ee.aleksale.common.proto.v1.CommerceResponse;
import ee.aleksale.management.proto.v1.ClientManagementGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ManagementClientGrpcService extends ClientManagementGrpc.ClientManagementImplBase {

    private final ClientServiceGrpc.ClientServiceBlockingStub clientServiceBlockingStub;

    @Override
    public void registerClient(Client request, StreamObserver<CommerceResponse> responseObserver) {
        var response = clientServiceBlockingStub.registerClient(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
