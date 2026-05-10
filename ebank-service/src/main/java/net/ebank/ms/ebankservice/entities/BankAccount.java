package net.ebank.ms.ebankservice.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.*;
import net.ebank.ms.ebankservice.model.Customer;

import java.util.Date;

/**
 * Created by lakbir.abderrahim on 03/02/2026
 */

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class BankAccount {

    @Id
    private String id;
    private Date createdAt;
    private double balance;
    private String type;
    private long customerId;

    @Transient
    private Customer customer;
}
