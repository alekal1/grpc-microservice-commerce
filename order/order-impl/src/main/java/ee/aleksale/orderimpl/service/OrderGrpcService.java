package ee.aleksale.orderimpl.service;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.payment.proto.v1.Payment;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.inventory.proto.v1.InventoryAvailabilityServiceGrpc;
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
    private final InventoryAvailabilityServiceGrpc.InventoryAvailabilityServiceBlockingStub inventoryAvailabilityServiceBlockingStub;

    @Override
    public StreamObserver<InventoryOrder> makeOrder(StreamObserver<CommerceResponse> responseObserver) {
        return new StreamObserver<>() {

            private final CurrentOrderService currentOrderService = new CurrentOrderService();

            @Override
            public void onNext(InventoryOrder inventoryOrder) {
                var availabilityResponse = inventoryAvailabilityServiceBlockingStub.checkAvailability(
                        InventoryOrder.newBuilder()
                                .setName(inventoryOrder.getName())
                                .setType(inventoryOrder.getType())
                                .setQuantity(currentOrderService.getTotalQuantity(inventoryOrder))
                                .build()
                );

                if (availabilityResponse.hasError()) {
                    responseObserver.onNext(availabilityResponse);
                    responseObserver.onCompleted();
                }

                currentOrderService.add(inventoryOrder);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                var paymentBuilder = Payment.newBuilder();
                paymentBuilder.addAllInventoryOrders(currentOrderService.collectOrderWithTotalQuantity());

                paymentServiceStub.processPayment(paymentBuilder.build(), responseObserver);

                currentOrderService.clear();
            }
        };
    }
}
