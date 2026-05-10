package net.ebank.ms.customerservice;

import net.ebank.ms.customerservice.entities.Customer;
import net.ebank.ms.customerservice.services.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerService customerService) {
        return args -> {
            List<String> customers = List.of("Lakbir", "Abderrahim", "John Doe");
            customers.forEach(c -> {
                customerService.save(Customer.builder()
                        .firstName(c)
                        .lastName("Smith")
                        .email(c+"@gmail.com")
                        .build());
            });
        };
    }

}
