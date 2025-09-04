package ee.aleksale.client.service;

import ee.aleksale.client.exception.ClientException;
import ee.aleksale.client.model.domain.ClientEntity;
import ee.aleksale.client.repository.ClientRepository;
import ee.aleksale.common.client.proto.v1.Client;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client saveClient(Client client) {
        var entity = new ClientEntity();

        entity.setName(client.getName());
        entity.setIdentifierCode(UUID.randomUUID());
        entity.setMoney(client.getMoney());

        var savedEntity = clientRepository.saveAndFlush(entity);

        return Client.newBuilder()
                .setName(savedEntity.getName())
                .setIdentifierCode(savedEntity.getIdentifierCode().toString())
                .setMoney(savedEntity.getMoney())
                .build();
    }

    @Transactional
    public Client addMoney(String identificationCode, double money) {
        if (money < 0) {
            throw new ClientException("Money cannot be less than zero");
        }

        var entity = clientRepository.findByIdentifierCode(UUID.fromString(identificationCode))
                .orElseThrow(() -> new ClientException("Client not found!"));

        clientRepository.updateMoney(entity.getId(), entity.getMoney() + money);

        return Client.newBuilder()
                .setName(entity.getName())
                .setIdentifierCode(entity.getIdentifierCode().toString())
                .setMoney(entity.getMoney() + money)
                .build();
    }
}
