package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.exception.ResourceNotFoundException;
import com.br.pi4.artinlife.model.*;
import com.br.pi4.artinlife.repository.OrderItemRepository;
import com.br.pi4.artinlife.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    public List<Order> getOrdersByClient(Client client) {
        return orderRepository.findByClient(client);
    }

    public List<OrderItem> getOrderItemsByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + orderId));
    }

    public Order checkout(Client client, ClientAddress address, PaymentDetails paymentDetails) {
        Cart cart = cartService.getCartByClient(client);
        List<CartItem> cartItems = cartService.getItemsByCart(cart);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Carrinho vazio");
        }

        Order order = new Order();
        order.setClient(client);
        order.setAddress(address);
        order.setPaymentDetails(paymentDetails);
        order.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
        order.setOrderDate(LocalDateTime.now());

        order = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItemRepository.save(orderItem);
        }

        cartService.clearCart(cart);

        return order;
    }
}
