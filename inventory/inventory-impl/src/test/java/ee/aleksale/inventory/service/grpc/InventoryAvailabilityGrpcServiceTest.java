package ee.aleksale.inventory.service.grpc;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.service.InventoryService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class InventoryAvailabilityGrpcServiceTest {

    private InventoryService inventoryService;

    private InventoryAvailabilityGrpcService inventoryAvailabilityGrpcService;

    @BeforeEach
    void init() {
        inventoryService = mock(InventoryService.class);

        inventoryAvailabilityGrpcService = new InventoryAvailabilityGrpcService(
                inventoryService
        );
    }

    @Test
    void checkAvailability_unitNotFound() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var request = InventoryOrder.getDefaultInstance();

        doReturn(null)
                .when(inventoryService)
                .getInventory(request);

        inventoryAvailabilityGrpcService.checkAvailability(request, responseObserver);

        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);

        assertTrue(commerceResponse.hasError());
        var error = commerceResponse.getError();

        assertEquals(404, error.getCode());
        assertNotNull(error.getMessage());
    }

    @Test
    void checkAvailability_insufficientInventory() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var request = InventoryOrder.getDefaultInstance();

        doThrow(new InventoryException("insufficient"))
                .when(inventoryService)
                .getInventory(request);

        inventoryAvailabilityGrpcService.checkAvailability(request, responseObserver);

        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);

        assertTrue(commerceResponse.hasError());
        var error = commerceResponse.getError();

        assertEquals(400, error.getCode());
        assertNotNull(error.getMessage());
    }

    @Test
    void checkAvailability() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var request = InventoryOrder.getDefaultInstance();

        doReturn(InventoryUnit.getDefaultInstance())
                .when(inventoryService)
                .getInventory(request);

        inventoryAvailabilityGrpcService.checkAvailability(request, responseObserver);

        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);

        assertNotNull(commerceResponse.getSuccess());
    }
}
