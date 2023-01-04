package tn.ipsas.clientseervice.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tn.ipsas.clientseervice.service.ClientService;
import tn.ipsas.coremodels.models.client.Client;

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

    @GetMapping("{id}")
    public Client byId(@PathVariable("id") String id) {
        return service.getById(id);
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
