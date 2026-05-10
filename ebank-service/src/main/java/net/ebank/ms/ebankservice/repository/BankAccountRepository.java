package net.ebank.ms.ebankservice.repository;


import net.ebank.ms.ebankservice.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lakbir.abderrahim on 03/02/2026
 */

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByCustomerId(long customerId);
}
