package ee.aleksale.inventory.repository;

import ee.aleksale.common.proto.v1.InventoryUnit;
import ee.aleksale.inventory.model.domain.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByNameAndInventoryType(String name, InventoryUnit.InventoryType inventoryType);

    void deleteById(@NonNull Long id);

    @Modifying
    @Query("UPDATE InventoryEntity e SET e.quantity = :quantity WHERE e.id = :id")
    void updateQuantity(@Param(value = "id") Long id, @Param(value = "quantity") Long quantity);
}
