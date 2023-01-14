package tn.ipsas.clientseervice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tn.ipsas.clientseervice.data.ClientRepository;
import tn.ipsas.clientseervice.feign.FactureFeign;
import tn.ipsas.clientseervice.feign.ReglementFeign;
import tn.ipsas.coremodels.dto.ClientDto;
import tn.ipsas.coremodels.dto.FactureReglementDTO;
import tn.ipsas.coremodels.exceptions.EntityNotFoundException;
import tn.ipsas.coremodels.models.client.Client;
import tn.ipsas.coremodels.models.produit.Product;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClientService {

    private final ClientRepository repository;
    private final FactureFeign factureFeign;
    private final ReglementFeign reglementFeign;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ClientService(
            ClientRepository repository,
            FactureFeign factureFeign, ReglementFeign reglementFeign, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.factureFeign = factureFeign;
        this.reglementFeign = reglementFeign;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Page<Client> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Client> getAll() {
        return repository.findAll();
    }
    public Client getById(String id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
    }

    private static class ProductOccurnce {
        private Product product;
        private int quantity;
    }
    public ClientDto getDetailsById(String id) {
        Client client = repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
        List<FactureReglementDTO> factureReglementDTOS = factureFeign.byClient(id, false);
        double chiffreAffaire = factureReglementDTOS.stream().mapToDouble(factureReglementDTO -> factureReglementDTO.getFacture().getTotal()).sum();
        HashMap<Integer, Double> chiffreAffaireParAn = factureReglementDTOS.stream()
                .filter(factureReglementDTO -> factureReglementDTO.getFacture().getDate() != null)
                .reduce(
                new HashMap<Integer, Double>(),
                (map, dto) -> {
                    int year = dto.getFacture().getDate().getYear();
                    map.put(year, map.getOrDefault(year, 0d) + dto.getFacture().getTotal());
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((year, montant) -> {
                        map1.put(year, map1.getOrDefault(year, 0d) + map2.get(year));
                    });
                    return map1;
                }
        );
        int year = LocalDate.now().getYear();
        if (!chiffreAffaireParAn.isEmpty()) {
            if (!chiffreAffaireParAn.containsKey(year)) {
                chiffreAffaireParAn.put(year--, 0d);
            }
        }
        double needToPay = factureReglementDTOS.stream().mapToDouble(FactureReglementDTO::getNeedToPay).sum();
        if (needToPay <= 0) {
            needToPay = -reglementFeign.solde(id);
        }
        List<FactureReglementDTO> regle = factureReglementDTOS.stream().filter(factureReglementDTO -> factureReglementDTO.getNeedToPay() == 0).collect(Collectors.toList());
        List<FactureReglementDTO> nonRegle = factureReglementDTOS.stream().filter(factureReglementDTO -> factureReglementDTO.getNeedToPay() > 0).collect(Collectors.toList());
        List<ClientDto.ProductPrefere> productPreferes = factureReglementDTOS
                .stream()
                .flatMap(factureReglementDTO -> factureReglementDTO.getFacture().getItems().stream())
                .reduce(new HashMap<String, ProductOccurnce>(), (map, item) -> {
                    if (map.containsKey(item.getProduct().getId())) {
                        ProductOccurnce a = map.get(item.getProduct().getId());
                        a.quantity += item.getQuantity();
                    } else {
                        ProductOccurnce a = new ProductOccurnce();
                        a.quantity = item.getQuantity();
                        a.product = item.getProduct();
                        map.put(item.getProduct().getId(), a);
                    }
                    return map;
                }, (map1, map2) -> {
                    map2.forEach((s, item) -> {
                        if (map1.containsKey(s)) {
                            ProductOccurnce a = map1.get(s);
                            a.quantity += item.quantity;
                        } else {
                            ProductOccurnce a = new ProductOccurnce();
                            a.quantity = item.quantity;
                            a.product = item.product;
                            map1.put(s, a);
                        }
                    });
                    return map1;
                }).values().stream().map(o -> new ClientDto.ProductPrefere(o.product, o.quantity)).collect(Collectors.toList());

        ClientDto dto = new ClientDto();
        dto.setClient(client);
        dto.setChiffreAffaire(chiffreAffaire);
        dto.setChiffreAffaireParAn(chiffreAffaireParAn);
        dto.setFactureRegle(regle);
        dto.setFactureNonRegle(nonRegle);
        dto.setProduitSollicite(productPreferes);
        dto.setMontantNonPaye(needToPay);
        return dto;
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
