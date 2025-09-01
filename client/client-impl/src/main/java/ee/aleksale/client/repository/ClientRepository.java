package ee.aleksale.client.repository;

import ee.aleksale.client.model.domain.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
}
