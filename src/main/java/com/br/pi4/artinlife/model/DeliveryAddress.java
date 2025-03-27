package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "delivery_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddress {

    @Id
    private String id = UUID.randomUUID().toString();

    // Cliente dono do endereço
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private String cep;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
}
