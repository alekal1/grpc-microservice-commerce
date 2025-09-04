package ee.aleksale.inventory.service.grpc;

import ee.aleksale.common.response.proto.v1.CommerceResponse;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.service.InventoryService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class InventoryManagementGrpcServiceTest {

    private InventoryService inventoryService;

    private InventoryManagementGrpcService inventoryManagementGrpcService;

    @BeforeEach
    void init() {
        inventoryService = mock(InventoryService.class);

        inventoryManagementGrpcService = new InventoryManagementGrpcService(
                inventoryService
        );
    }

    @Test
    void addInventoryUnit_success() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = inventoryManagementGrpcService.addInventoryUnit(responseObserver);

        var unit = InventoryUnit.getDefaultInstance();
        doReturn(unit)
                .when(inventoryService)
                .saveInventory(unit);

        requestObserver.onNext(unit);

        assertFalse(responseObserver.getValues().isEmpty());
        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);
        assertNotNull(commerceResponse.getSuccess());

        assertFalse(commerceResponse.getSuccess().getDataList().isEmpty());
        assertEquals(1, commerceResponse.getSuccess().getDataList().size());
    }

    @Test
    void addInventory_error() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = inventoryManagementGrpcService.addInventoryUnit(responseObserver);

        var unit = InventoryUnit.getDefaultInstance();
        doThrow(new InventoryException("error from service"))
                .when(inventoryService)
                .saveInventory(unit);

        requestObserver.onNext(unit);

        assertFalse(responseObserver.getValues().isEmpty());
        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);
        assertNotNull(commerceResponse.getError());

        var error = commerceResponse.getError();

        assertNotNull(error.getMessage());
    }

    @Test
    void removeInventory_success() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = inventoryManagementGrpcService.removeInventoryUnit(responseObserver);

        var unit = InventoryUnit.getDefaultInstance();

        requestObserver.onNext(unit);

        assertFalse(responseObserver.getValues().isEmpty());
        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);
        assertNotNull(commerceResponse.getSuccess());

        assertNotNull(commerceResponse.getSuccess().getMessage());
    }

    @Test
    void removeInventory_error() {
        var responseObserver = StreamRecorder.<CommerceResponse>create();
        var requestObserver = inventoryManagementGrpcService.removeInventoryUnit(responseObserver);

        var unit = InventoryUnit.getDefaultInstance();
        doThrow(new InventoryException("error from service"))
                .when(inventoryService)
                .removeInventory(unit);

        requestObserver.onNext(unit);

        assertFalse(responseObserver.getValues().isEmpty());
        assertEquals(1, responseObserver.getValues().size());

        var commerceResponse = responseObserver.getValues().get(0);
        assertNotNull(commerceResponse.getError());

        var error = commerceResponse.getError();

        assertNotNull(error.getMessage());
    }
}
