package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Cliente que fez o pedido (pode ser null no caso de compra anônima)
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Endereço de entrega
    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private ClientAddress address;

    // Lista de itens comprados
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_details_id")
    private PaymentDetails paymentDetails;

    private LocalDateTime orderDate;

    // Valor total do pedido
    private BigDecimal totalPrice;

    // Status do pedido (ex: PENDING, SENT, DELIVERED, CANCELED)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
}
