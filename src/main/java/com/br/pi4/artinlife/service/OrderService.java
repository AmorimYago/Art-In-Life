package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.model.*;
import com.br.pi4.artinlife.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * Gera um pedido com base no carrinho e endereço de entrega.
     */
    public Order createOrderFromCart(Cart cart, ClientAddress address) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();

            total = total.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .clientAddress(address)
                .items(orderItems)
                .totalPrice(total)
                .status(OrderStatus.PENDING)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        orderRepository.save(order);
        return order;
    }
}
