package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    private String id = UUID.randomUUID().toString();

    // Produto comprado
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Pedido ao qual esse item pertence
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Quantidade de unidades compradas
    private Integer quantity;

    // Preço unitário na hora da compra
    private BigDecimal unitPrice;
}
