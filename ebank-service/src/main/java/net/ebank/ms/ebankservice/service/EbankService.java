package net.ebank.ms.ebankservice.service;


import lombok.AllArgsConstructor;
import net.ebank.ms.ebankservice.entities.BankAccount;
import net.ebank.ms.ebankservice.feign.CustomerRestClient;
import net.ebank.ms.ebankservice.model.Customer;
import net.ebank.ms.ebankservice.repository.BankAccountRepository;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
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

    @McpTool(description = "Get all  bank accounts")
    public List<BankAccount> getBankAccounts(){
        return bankAccountRepository.findAll();
    }

    @McpTool(description = "get a bank account by id")
    public BankAccount getBankAccount(@McpToolParam(description = "The bank account id") String id){
        BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new RuntimeException("Bank account not found"));

        bankAccount.setCustomer(customerRestClient.getCustomer(bankAccount.getCustomerId()));
        return bankAccount;
    }


    public List<BankAccount> getBankAccountsByCustomerId(long customerId){
        return bankAccountRepository.findByCustomerId(customerId);
    }

    @McpTool(description = "Save a new bank account")
    public BankAccount saveBankAccount(@McpToolParam(description = "The bank account to save(balance, type, customerId)") BankAccount bankAccount){
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