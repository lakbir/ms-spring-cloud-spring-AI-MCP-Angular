package net.ebank.ms.ebankservice.service;


import lombok.AllArgsConstructor;
import net.ebank.ms.ebankservice.entities.BankAccount;
import net.ebank.ms.ebankservice.feign.CustomerRestClient;
import net.ebank.ms.ebankservice.model.Customer;
import net.ebank.ms.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by lakbir.abderrahim on 03/02/2026
 */

@Service
@AllArgsConstructor
public class EbankService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRestClient customerRestClient;

    public List<BankAccount> getBankAccounts(){
        return bankAccountRepository.findAll();
    }

    public BankAccount getBankAccount( String id){
        BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new RuntimeException("Bank account not found"));

        bankAccount.setCustomer(customerRestClient.getCustomer(bankAccount.getCustomerId()));
        return bankAccount;
    }


    public List<BankAccount> getBankAccountsByCustomerId(long customerId){
        return bankAccountRepository.findByCustomerId(customerId);
    }

    public BankAccount saveBankAccount( BankAccount bankAccount){
        try {
            Customer customer = customerRestClient.getCustomer(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            bankAccount.setCustomer(customer);
            return bankAccountRepository.save(bankAccount);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }
}