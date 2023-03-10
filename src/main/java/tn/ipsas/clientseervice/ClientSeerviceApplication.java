package tn.ipsas.clientseervice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan("tn.ipsas")
public class ClientSeerviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientSeerviceApplication.class, args);
    }

}
