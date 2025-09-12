package ee.aleksale.paymentimpl.service;

import ee.aleksale.common.payment.proto.v1.Payment;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.credentials.CommerceSecretCallInterceptor;
import ee.aleksale.payment.proto.v1.PaymentServiceGrpc;
import ee.aleksale.paymentimpl.interceptors.service.RetrieveIdentificationInterceptor;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService(interceptors = {
        CommerceSecretCallInterceptor.class,
        RetrieveIdentificationInterceptor.class
})
@RequiredArgsConstructor
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Override
    public void processPayment(Payment request, StreamObserver<CommerceResponse> responseObserver) {
        // TODO: remove money from client
        log.info("Payment request {}", request);
        responseObserver.onNext(CommerceResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
