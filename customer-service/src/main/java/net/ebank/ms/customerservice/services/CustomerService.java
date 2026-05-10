package net.ebank.ms.customerservice.services;


import lombok.AllArgsConstructor;
import net.ebank.ms.customerservice.entities.Customer;
import net.ebank.ms.customerservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by lakbir.abderrahim on 08/05/2026
 */
@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    //@McpTool(description = "Get all customers")
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

   // @McpTool(description = "Fin a customer by id")
    public Customer findById( Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

   // @McpTool(description = "Save a new customer")
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
}
