package ee.aleksale.inventory.service.validator;

import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.model.domain.InventoryEntity;
import ee.aleksale.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryValidator {

    private final InventoryRepository inventoryRepository;

    public void validateAddInventory(InventoryUnit unit) throws InventoryException {
        if (unit.getPrice() <= 0) {
            throw new InventoryException("Price cannot be less than zero.");
        }

        inventoryRepository.findByNameAndInventoryType(unit.getName(), unit.getType())
                .ifPresent(v -> {
                    throw new InventoryException(String.format("Inventory '%s' with type '%s' already exists!",
                            unit.getName(), unit.getType()));
                });
    }

    public void validateRemoveInventory(Optional<InventoryEntity> entity, InventoryUnit unit) throws InventoryException {
        if (entity.isEmpty()) {
            throw new InventoryException("Inventory unit not found.");
        }
        if (entity.get().getQuantity() < unit.getQuantity()) {
            throw new InventoryException("Cannot delete more than have.");
        }
    }
}
