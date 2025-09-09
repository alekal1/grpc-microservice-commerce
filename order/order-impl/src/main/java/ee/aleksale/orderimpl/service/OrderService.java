package ee.aleksale.orderimpl.service;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.common.payment.proto.v1.Payment;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.inventory.proto.v1.InventoryAvailabilityServiceGrpc;
import ee.aleksale.orderimpl.exception.OrderException;
import ee.aleksale.payment.proto.v1.PaymentServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final InventoryAvailabilityServiceGrpc.InventoryAvailabilityServiceBlockingStub inventoryAvailabilityServiceBlockingStub;
    private final PaymentServiceGrpc.PaymentServiceFutureStub paymentFutureStub;

    public InventoryUnit inventoryAvailabilityResponse(InventoryOrder order) {
        var availabilityResponse = inventoryAvailabilityServiceBlockingStub.checkAvailability(order);

        if (availabilityResponse.hasError()) {
            throw new OrderException(availabilityResponse.getError().getMessage());
        }

        try {
            assert availabilityResponse.getSuccess().getDataCount() == 1;

            return availabilityResponse.getSuccess().getData(0).unpack(InventoryUnit.class);
        } catch (Exception e) {
            throw new OrderException(e.getMessage());
        }
    }

    public void proceedToPayment(Payment payment) {
        var paymentResponse = paymentFutureStub.processPayment(payment);
        CommerceResponse processPaymentResponse;
        try {
            processPaymentResponse = paymentResponse.get(10L, TimeUnit.SECONDS);

            if (processPaymentResponse == null) {
                throw new OrderException("No response from payment service");
            }

            if (processPaymentResponse.hasError()) {
                throw new OrderException(processPaymentResponse.getError().getMessage());
            }
        } catch (Exception e) {
            throw new OrderException(e.getMessage());
        }
    }
}
