package ee.aleksale.orderimpl.service;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.inventory.proto.v1.InventoryType;
import ee.aleksale.common.payment.proto.v1.Payment;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.inventory.proto.v1.InventoryAvailabilityServiceGrpc;
import ee.aleksale.order.proto.v1.OrderServiceGrpc;
import ee.aleksale.payment.proto.v1.PaymentServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.HashMap;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final PaymentServiceGrpc.PaymentServiceStub paymentServiceStub;
    private final InventoryAvailabilityServiceGrpc.InventoryAvailabilityServiceBlockingStub inventoryAvailabilityServiceBlockingStub;

    @Override
    public StreamObserver<InventoryOrder> makeOrder(StreamObserver<CommerceResponse> responseObserver) {
        var currentOrders = new HashMap<String, Long>();

        return new StreamObserver<>() {
            @Override
            public void onNext(InventoryOrder inventoryOrder) {
                var key = String.format("%s_%s", inventoryOrder.getName(), inventoryOrder.getType());
                var totalQuantity = currentOrders.getOrDefault(key, 0L) + inventoryOrder.getQuantity();

                var availabilityResponse = inventoryAvailabilityServiceBlockingStub.checkAvailability(
                        InventoryOrder.newBuilder()
                                .setName(inventoryOrder.getName())
                                .setType(inventoryOrder.getType())
                                .setQuantity(totalQuantity)
                                .build()
                );

                if (availabilityResponse.hasError()) {
                    responseObserver.onNext(availabilityResponse);
                    responseObserver.onCompleted();
                }

                currentOrders.put(key, totalQuantity);
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                var paymentBuilder = Payment.newBuilder();
                currentOrders.entrySet().stream()
                                .map(entry -> {
                                    var split = entry.getKey().split("_");
                                    return InventoryOrder.newBuilder()
                                            .setName(split[0])
                                            .setType(InventoryType.valueOf(split[1]))
                                            .setQuantity(entry.getValue());
                                })
                                .forEach(paymentBuilder::addInventoryOrders);

                paymentServiceStub.processPayment(paymentBuilder.build(), responseObserver);
            }
        };
    }
}
