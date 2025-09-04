package ee.aleksale.orderimpl.service;

import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.order.proto.v1.OrderServiceGrpc;
import ee.aleksale.payment.proto.v1.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final PaymentServiceGrpc.PaymentServiceStub paymentServiceStub;

    @Override
    public StreamObserver<InventoryUnit> makeOrder(StreamObserver<CommerceResponse> responseObserver) {
        return super.makeOrder(responseObserver);
    }
}
