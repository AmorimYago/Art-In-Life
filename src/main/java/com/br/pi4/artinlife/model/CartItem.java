package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cart_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    private String id = UUID.randomUUID().toString();

    // Produto adicionado ao carrinho
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Carrinho ao qual esse item pertence
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Quantidade do produto nesse item
    private Integer quantity;

    // Preço unitário do produto no momento em que foi adicionado
    private BigDecimal unitPrice;
}
