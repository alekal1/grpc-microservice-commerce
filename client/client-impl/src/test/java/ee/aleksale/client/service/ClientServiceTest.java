package ee.aleksale.client.service;

import ee.aleksale.client.exception.ClientException;
import ee.aleksale.client.model.domain.ClientEntity;
import ee.aleksale.client.repository.ClientRepository;
import ee.aleksale.common.client.proto.v1.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    private ClientRepository clientRepository;
    ArgumentCaptor<ClientEntity> clientEntityArgumentCaptor;


    private ClientService clientService;

    @BeforeEach
    void init() {
        clientRepository = mock(ClientRepository.class);

        clientService = new ClientService(clientRepository);

        clientEntityArgumentCaptor = ArgumentCaptor.forClass(ClientEntity.class);
    }

    @Test
    void saveClient() {
        var request = Client.getDefaultInstance();
        var entity = new ClientEntity();
        entity.setName("anyName");
        entity.setIdentifierCode(UUID.randomUUID());
        entity.setMoney(1.0);

        doReturn(entity)
                .when(clientRepository)
                .saveAndFlush(any(ClientEntity.class));

        var response = clientService.saveClient(request);

        assertEquals(entity.getName(), response.getName());
        assertEquals(entity.getIdentifierCode().toString(), response.getIdentifierCode());
        assertEquals(entity.getMoney(), response.getMoney());
    }


    @Test
    void addMoney() {
        var uuid = UUID.randomUUID();

        var entity = new ClientEntity();
        entity.setId(1L);
        entity.setMoney(10.0);
        entity.setName("anyName");
        entity.setIdentifierCode(uuid);

        var requestMoney = 5.0;

        doReturn(Optional.of(entity))
                .when(clientRepository)
                .findByIdentifierCode(uuid);

        var response = clientService.addMoney(uuid.toString(), requestMoney);

        verify(clientRepository).updateMoney(entity.getId(), entity.getMoney() + requestMoney);

        assertEquals(entity.getName() , response.getName());
        assertEquals(entity.getIdentifierCode().toString(), response.getIdentifierCode());
        assertEquals(entity.getMoney() + requestMoney, response.getMoney());
    }

    @Test
    void addMoney_negative() {
        assertThrows(ClientException.class, () ->
                clientService.addMoney(UUID.randomUUID().toString(), -1.0)
        );
    }

    @Test
    void addMoney_clientNotFound() {
        doReturn(Optional.empty())
                .when(clientRepository)
                .findByIdentifierCode(any(UUID.class));

        assertThrows(ClientException.class, () ->
                clientService.addMoney(UUID.randomUUID().toString(), 10.0)
        );
    }

}
