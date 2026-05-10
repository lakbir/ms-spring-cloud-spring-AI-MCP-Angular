package net.ebank.ms.customerservice.controller;


import lombok.AllArgsConstructor;
import net.ebank.ms.customerservice.entities.Customer;
import net.ebank.ms.customerservice.services.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lakbir.abderrahim on 08/05/2026
 */

@RestController
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerRestController {

    private final CustomerService customerService;

    @GetMapping("")
    public List<Customer> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Customer findById(@PathVariable Long id) {
        return  customerService.findById(id);
    }

    @PostMapping("")
    public Customer save(@RequestBody Customer customer) {
        return customerService.save(customer);
    }
}
