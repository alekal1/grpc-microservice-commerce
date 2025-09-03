package ee.aleksale.client.repository;

import ee.aleksale.client.model.domain.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByIdentifierCode(UUID identifierCode);

    @Modifying
    @Query("UPDATE ClientEntity e SET e.money = :money WHERE e.id = :id")
    void updateMoney(@Param(value = "id") Long id, @Param(value = "money") Double money);
}
