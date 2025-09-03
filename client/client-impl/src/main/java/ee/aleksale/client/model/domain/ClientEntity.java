package ee.aleksale.client.model.domain;

import ee.aleksale.client.model.converter.UUIDConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "client", catalog = "clientdb", schema = "client")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Convert(converter = UUIDConverter.class)
    @Column(name = "identifier_code")
    private UUID identifierCode;

    @Column(name = "money")
    private Double money;
}
