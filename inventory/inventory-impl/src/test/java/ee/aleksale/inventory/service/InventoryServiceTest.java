package ee.aleksale.inventory.service;

import ee.aleksale.common.inventory.proto.v1.InventoryType;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.model.domain.InventoryEntity;
import ee.aleksale.inventory.repository.InventoryRepository;
import ee.aleksale.inventory.service.validator.InventoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private InventoryValidator inventoryValidator;

    private InventoryService inventoryService;

    ArgumentCaptor<InventoryEntity> inventoryEntityArgumentCaptor;

    @BeforeEach
    void init() {
        inventoryRepository = mock(InventoryRepository.class);
        inventoryValidator = mock(InventoryValidator.class);

        inventoryService = new InventoryService(inventoryRepository, inventoryValidator);

        inventoryEntityArgumentCaptor = ArgumentCaptor.forClass(InventoryEntity.class);
    }

    @Test
    void saveInventory() {
        var request = InventoryUnit.newBuilder()
                .setName("anyName")
                .setType(InventoryType.HARDWARE)
                .setPrice(1.0)
                .setQuantity(1L)
                .build();
        var inventoryUnitEntity = new InventoryEntity();
        inventoryUnitEntity.setName("anyName");
        inventoryUnitEntity.setInventoryType(InventoryType.HARDWARE);
        inventoryUnitEntity.setPrice(1.0);
        inventoryUnitEntity.setQuantity(1L);

        doReturn(inventoryUnitEntity)
                .when(inventoryRepository)
                .saveAndFlush(any());

        inventoryService.saveInventory(request);

        verify(inventoryRepository).saveAndFlush(inventoryEntityArgumentCaptor.capture());
        var savedEntity = inventoryEntityArgumentCaptor.getValue();

        assertEquals(request.getName(), savedEntity.getName());
        assertEquals(request.getType(), savedEntity.getInventoryType());
        assertEquals(request.getPrice(), savedEntity.getPrice());
        assertEquals(request.getQuantity(), savedEntity.getQuantity());
    }

    @Test
    void removeInventory_validationError() {
        var request = InventoryUnit.getDefaultInstance();
        var optionalEntity = Optional.of(new InventoryEntity());

        doReturn(optionalEntity)
                .when(inventoryRepository)
                .findByNameAndInventoryType(request.getName(), request.getType());

        doThrow(InventoryException.class)
                .when(inventoryValidator)
                .validateRemoveInventory(optionalEntity, request);

        assertThrows(InventoryException.class, () -> inventoryService.removeInventory(request));
    }

    @Test
    void removeInventory_delete() {
        var request = InventoryUnit.newBuilder()
                .setName("anyName")
                .setType(InventoryType.HARDWARE)
                .setQuantity(1)
                .build();
        var entity = new InventoryEntity();
        entity.setId(1L);
        entity.setQuantity(request.getQuantity());

        doReturn(Optional.of(entity))
                .when(inventoryRepository)
                .findByNameAndInventoryType(request.getName(), request.getType());

        inventoryService.removeInventory(request);

        verify(inventoryRepository).deleteById(entity.getId());
        verify(inventoryRepository, times(0)).updateQuantity(entity.getId(), entity.getQuantity());
    }

    @Test
    void removeInventory_update() {
        var request = InventoryUnit.newBuilder()
                .setName("anyName")
                .setType(InventoryType.HARDWARE)
                .setQuantity(1)
                .build();
        var entity = new InventoryEntity();
        entity.setId(2L);
        entity.setQuantity(request.getQuantity() + 1);

        doReturn(Optional.of(entity))
                .when(inventoryRepository)
                .findByNameAndInventoryType(request.getName(), request.getType());

        inventoryService.removeInventory(request);

        verify(inventoryRepository, times(0)).deleteById(entity.getId());
        verify(inventoryRepository).updateQuantity(entity.getId(), entity.getQuantity() - request.getQuantity());
    }
}
