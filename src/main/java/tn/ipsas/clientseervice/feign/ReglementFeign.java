package tn.ipsas.clientseervice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "reglement-service", path = "reglement")
public interface ReglementFeign {
    @GetMapping("solde/{clientId}")
    public double solde(@PathVariable("clientId") String clientId);
}
