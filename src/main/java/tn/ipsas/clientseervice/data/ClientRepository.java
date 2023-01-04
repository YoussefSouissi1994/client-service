package tn.ipsas.clientseervice.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.ipsas.coremodels.models.client.Client;

public interface ClientRepository extends MongoRepository<Client, String> {
    boolean existsByEmail(String email);
}
