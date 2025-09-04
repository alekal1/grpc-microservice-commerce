package ee.aleksale.inventory.service.validator;

import ee.aleksale.common.inventory.proto.v1.InventoryType;
import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.model.domain.InventoryEntity;
import ee.aleksale.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class InventoryValidatorTest {

    private InventoryRepository inventoryRepository;

    private InventoryValidator inventoryValidator;

    @BeforeEach
    void init() {
        inventoryRepository = mock(InventoryRepository.class);

        inventoryValidator = new InventoryValidator(inventoryRepository);
    }

    private static Stream<Arguments> negativeValues() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-0),
                Arguments.of(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("negativeValues")
    void addInventoryUnit_negativePrice(double price) {
        var inventoryUnit = InventoryUnit.newBuilder()
                        .setPrice(price)
                        .build();
        assertThrows(InventoryException.class, () -> inventoryValidator.validateAddInventory(inventoryUnit));
    }

    @Test
    void addInventory_alreadyExists() {
        var inventoryUnit = InventoryUnit.newBuilder()
                .setName("anyName")
                .setType(InventoryType.HARDWARE)
                .setPrice(10L)
                .build();

        var entity = new InventoryEntity();

        doReturn(Optional.of(entity))
                .when(inventoryRepository)
                .findByNameAndInventoryType(inventoryUnit.getName(), inventoryUnit.getType());

        assertThrows(InventoryException.class, () -> inventoryValidator.validateAddInventory(inventoryUnit));
    }

    @Test
    void removeInventory_entityNotFound() {
        assertThrows(InventoryException.class, () -> inventoryValidator.validateRemoveInventory(Optional.empty(), InventoryUnit.getDefaultInstance()));
    }

    @Test
    void removeInventory_invalidQuantity() {
        var entity = new InventoryEntity();
        entity.setQuantity(1L);

        var unit = InventoryUnit.newBuilder()
                .setQuantity(2L)
                .build();

        assertThrows(InventoryException.class, () -> inventoryValidator.validateRemoveInventory(Optional.of(entity), unit));
    }
}
