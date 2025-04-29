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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Produto adicionado ao carrinho
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Carrinho ao qual esse item pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // Quantidade do produto nesse item
    @Column(nullable = false)
    private Integer quantity;

    // Preço unitário do produto no momento em que foi adicionado
    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}
