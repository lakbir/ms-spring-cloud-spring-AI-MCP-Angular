package net.ebank.ms.ebankservice.controller;


import lombok.AllArgsConstructor;
import net.ebank.ms.ebankservice.entities.BankAccount;
import net.ebank.ms.ebankservice.service.EbankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lakbir.abderrahim on 03/02/2026
 */

@AllArgsConstructor
@RestController
@RequestMapping("/accounts")
public class RestBankAccountController {

    private final EbankService ebankService;

    @GetMapping
    public List<BankAccount> getBankAccounts(){
        return ebankService.getBankAccounts();
    }

    @GetMapping("/{id}")
    public BankAccount getBankAccount(@PathVariable String id){
        return ebankService.getBankAccount(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<BankAccount> getBankAccountsByCustomerId(@PathVariable long customerId){
        return ebankService.getBankAccountsByCustomerId(customerId);
    }

    @PostMapping
    public BankAccount saveBankAccount(@RequestBody BankAccount bankAccount){
        return ebankService.saveBankAccount(bankAccount);
    }
}
