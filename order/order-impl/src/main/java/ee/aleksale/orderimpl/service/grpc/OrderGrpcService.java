package ee.aleksale.orderimpl.service.grpc;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.payment.proto.v1.Payment;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.response.proto.v1.ErrorResponse;
import ee.aleksale.common.response.proto.v1.SuccessResponse;
import ee.aleksale.credentials.CommerceSecretCallInterceptor;
import ee.aleksale.order.proto.v1.OrderServiceGrpc;
import ee.aleksale.orderimpl.exception.OrderException;
import ee.aleksale.orderimpl.interceptors.service.RetrieveIdentificationInterceptor;
import ee.aleksale.orderimpl.service.CurrentOrderUtils;
import ee.aleksale.orderimpl.service.OrderService;
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
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    @Override
    public StreamObserver<InventoryOrder> makeOrder(StreamObserver<CommerceResponse> responseObserver) {
        return new StreamObserver<>() {

            private final CurrentOrderUtils currentOrderUtils = new CurrentOrderUtils();

            @Override
            public void onNext(InventoryOrder inventoryOrder) {

                try {
                    var inventoryResponse = orderService.inventoryAvailabilityResponse(
                            InventoryOrder.newBuilder()
                                    .setName(inventoryOrder.getName())
                                    .setType(inventoryOrder.getType())
                                    .setQuantity(currentOrderUtils.getTotalQuantity(inventoryOrder))
                                    .build()
                    );

                    currentOrderUtils.add(inventoryOrder, inventoryResponse.getPrice());
                } catch (Exception e) {
                    responseObserver.onNext(CommerceResponse.newBuilder()
                            .setError(ErrorResponse.newBuilder()
                                    .setCode(500)
                                    .setMessage(e.getMessage())
                                    .build())
                            .build());
                    responseObserver.onCompleted();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                try {
                    orderService.proceedToPayment(
                            Payment.newBuilder()
                                    .addAllInventoryOrders(currentOrderUtils.getOrdersWithTotalQuantity())
                                    .setTotalSum(currentOrderUtils.getCurrentOrdersTotalSum())
                                    .build()
                    );

                    // TODO: Money XXX taken from your account
                    responseObserver.onNext(CommerceResponse.newBuilder()
                            .setSuccess(SuccessResponse.newBuilder()
                                    .setMessage("Order completed!")
                                    .build())
                            .build());

                    responseObserver.onCompleted();
                } catch (OrderException e) {
                    responseObserver.onNext(CommerceResponse.newBuilder()
                            .setError(ErrorResponse.newBuilder()
                                    .setCode(400)
                                    .setMessage(e.getMessage())
                                    .build())
                            .build());
                    responseObserver.onCompleted();
                } finally {
                    currentOrderUtils.clear();
                }
            }
        };
    }

}
