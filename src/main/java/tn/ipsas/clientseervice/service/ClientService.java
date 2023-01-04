package tn.ipsas.clientseervice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tn.ipsas.clientseervice.data.ClientRepository;
import tn.ipsas.coremodels.exceptions.EntityNotFoundException;
import tn.ipsas.coremodels.models.client.Client;


@Service
public class ClientService {

    private final ClientRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ClientService(
            ClientRepository repository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Page<Client> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
    public Client getById(String id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }
    public Client save(Client client) {
        Client clientSaved = repository.save(client);
        kafkaTemplate.send("client_add", clientSaved);
        return clientSaved;
    }
    public void delete(String id) {
        Client client = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        kafkaTemplate.send("client_delete", client);
        repository.deleteById(id);
    }


    public boolean exists(String id) {
        return repository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }


}
