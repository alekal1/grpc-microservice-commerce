package ee.aleksale.inventory.service;

import ee.aleksale.common.inventory.proto.v1.InventoryUnit;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.model.domain.InventoryEntity;
import ee.aleksale.inventory.repository.InventoryRepository;
import ee.aleksale.inventory.service.validator.InventoryValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryValidator inventoryValidator;

    @Transactional
    public InventoryUnit saveInventory(InventoryUnit unit) throws InventoryException {
        inventoryValidator.validateAddInventory(unit);

        var entity = new InventoryEntity();
        entity.setName(unit.getName());
        entity.setInventoryType(unit.getType());
        entity.setPrice(unit.getPrice());
        entity.setQuantity(unit.getQuantity());

        var savedEntity = inventoryRepository.saveAndFlush(entity);

        return InventoryUnit.newBuilder()
                .setName(savedEntity.getName())
                .setType(savedEntity.getInventoryType())
                .setPrice(savedEntity.getPrice())
                .setQuantity(savedEntity.getQuantity())
                .build();
    }

    @Transactional
    public void removeInventory(InventoryUnit unit) throws InventoryException {
        var entityOptional = inventoryRepository.findByNameAndInventoryType(unit.getName(), unit.getType());
        inventoryValidator.validateRemoveInventory(entityOptional, unit);

        var entity = entityOptional.get();

        if (entity.getQuantity() - unit.getQuantity() == 0) {
            inventoryRepository.deleteById(entity.getId());
            return;
        }

        inventoryRepository.updateQuantity(entity.getId(),
                entity.getQuantity() - unit.getQuantity());
    }
}
