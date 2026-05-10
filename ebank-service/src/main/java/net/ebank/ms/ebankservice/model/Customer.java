package net.ebank.ms.ebankservice.model;


import lombok.*;

/**
 * Created by lakbir.abderrahim on 03/02/2026
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
