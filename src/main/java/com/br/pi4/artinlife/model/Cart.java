package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    private String id = UUID.randomUUID().toString();

    // Usuário logado que possui o carrinho — pode ser null (anônimo)
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Soma total dos preços dos itens no carrinho
    private BigDecimal totalPrice = BigDecimal.ZERO;

    // Status do carrinho: aberto, finalizado, cancelado, etc.
    @Enumerated(EnumType.STRING)
    private CartStatus status = CartStatus.OPEN;

    // Lista de itens dentro do carrinho
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;
}
