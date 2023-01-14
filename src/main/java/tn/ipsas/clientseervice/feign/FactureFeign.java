package tn.ipsas.clientseervice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import tn.ipsas.coremodels.dto.FactureReglementDTO;

import java.util.List;

@FeignClient(name = "facture-service", path = "facture")
public interface FactureFeign {
    @GetMapping("byClient/{clientId}")
    List<FactureReglementDTO> byClient(
            @PathVariable("clientId") String clientId,
            @RequestParam(value = "onlyNotPaid", required = false, defaultValue = "false") boolean onlyNotPaid);
}
