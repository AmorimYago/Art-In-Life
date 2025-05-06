package com.br.pi4.artinlife.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "client_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    private Client client;

    @Column(nullable = false)
    @NotBlank(message = "O CEP é obrigatório")
    private String cep;

    @Column(nullable = false)
    @NotBlank(message = "O logradouro é obrigatório")
    private String street;

    @Column(nullable = false)
    @NotBlank(message = "O número é obrigatório")
    private String number;

    private String complement;

    @Column(nullable = false)
    @NotBlank(message = "O bairro é obrigatório")
    private String neighborhood;

    @Column(nullable = false)
    @NotBlank(message = "A cidade é obrigatória")
    private String city;

    @Column(nullable = false, length = 2)
    @NotBlank(message = "O estado é obrigatório")
    private String state;

    @Column(nullable = false)
    private boolean billingAddress;

    @Column(nullable = false)
    private boolean defaultDeliveryAddress;
}


