package ee.aleksale.inventory.service;

import ee.aleksale.common.proto.v1.InventoryUnit;
import ee.aleksale.inventory.exception.InventoryException;
import ee.aleksale.inventory.model.domain.InventoryEntity;
import ee.aleksale.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryUnit saveInventory(InventoryUnit unit) {
        //TODO: Mapstruct
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
    public void removeInventory(InventoryUnit unit) {
        var entity = inventoryRepository.findByNameAndInventoryType(unit.getName(), unit.getType())
                .orElseThrow(() -> new InventoryException("Inventory unit not found."));

        if (entity.getQuantity() < unit.getQuantity()) {
            throw new InventoryException("Cannot delete more than have.");
        }

        if (entity.getQuantity() - unit.getQuantity() == 0) {
            inventoryRepository.deleteById(entity.getId());
            return;
        }

        inventoryRepository.updateQuantity(entity.getId(), entity.getQuantity() - unit.getQuantity());
    }
}
