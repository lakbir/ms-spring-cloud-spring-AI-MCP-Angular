package net.ebank.ms.ebankservice.feign;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.ebank.ms.ebankservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by lakbir.abderrahim on 03/02/2026
 */

@FeignClient(name = "customer-service")
public interface CustomerRestClient {

    @GetMapping("/customers/{id}")
    @CircuitBreaker(name = "customer-service", fallbackMethod = "getCustomerFallback")
    Customer getCustomer(@PathVariable Long id);

    default Customer getCustomerFallback(Long id, Exception e) {
        return Customer.builder()
                .id(id)
                .firstName("Unknown")
                .lastName("Unknown")
                .email("Unknown")
                .build();
    }
}
