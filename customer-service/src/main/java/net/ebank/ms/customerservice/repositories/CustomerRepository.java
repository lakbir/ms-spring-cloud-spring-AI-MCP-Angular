package net.ebank.ms.customerservice.repositories;


import net.ebank.ms.customerservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lakbir.abderrahim on 08/05/2026
 */

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
