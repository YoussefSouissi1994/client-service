package tn.ipsas.clientseervice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.ipsas.clientseervice.service.ClientService;
import tn.ipsas.coremodels.dto.ClientDto;
import tn.ipsas.coremodels.models.client.Client;
import tn.ipsas.coremodels.models.produit.Product;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
public class ClientController {
    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Client> page(Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("all")
    public List<Client> getAll() {
        return service.getAll();
    }
    @GetMapping("{id}")
    public Client byId(@PathVariable("id") String id) {
        return service.getById(id);
    }
    @GetMapping("details/{id}")
    public ClientDto detailsById(@PathVariable("id") String id) {
        return service.getDetailsById(id);
    }

    @GetMapping("exists/{email}")
    public boolean exists(@PathVariable("email") String email) {
        return service.existsByEmail(email);
    }

    @PutMapping
    public Client add(@RequestBody Client client) {
        client.setId(null);
        return service.save(client);
    }

    @PutMapping("{id}")
    public Client update(@PathVariable("id") String id, @RequestBody Client client) {
        if (!service.exists(id)) {
            throw new IllegalArgumentException();
        }
        client.setId(id);
        return service.save(client);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") String id) {
        if (!service.exists(id)) {
            throw new IllegalArgumentException();
        }
        service.delete(id);
    }

}
