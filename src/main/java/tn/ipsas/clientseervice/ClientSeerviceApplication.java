package tn.ipsas.clientseervice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClientSeerviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientSeerviceApplication.class, args);
    }

}
