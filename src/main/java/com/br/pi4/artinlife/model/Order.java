package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders") // "order" é palavra reservada no SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    private String id = UUID.randomUUID().toString();

    // Cliente que fez o pedido (pode ser null no caso de compra anônima)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    // Endereço de entrega
    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private DeliveryAddress deliveryAddress;

    // Lista de itens comprados
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    // Valor total do pedido
    private BigDecimal totalPrice;

    // Status do pedido (ex: PENDING, SENT, DELIVERED, CANCELED)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
}
