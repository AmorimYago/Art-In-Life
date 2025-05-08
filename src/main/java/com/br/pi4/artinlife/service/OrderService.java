package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.OrderItemDTO;
import com.br.pi4.artinlife.exception.ResourceNotFoundException;
import com.br.pi4.artinlife.model.*;
import com.br.pi4.artinlife.repository.ClientAddressRepository;
import com.br.pi4.artinlife.repository.OrderItemRepository;
import com.br.pi4.artinlife.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ClientAddressRepository clientAddressRepository;

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

    public Order checkout(Client client,
                          Long addressId,
                          PaymentDetails paymentDetails,
                          PaymentMethod paymentMethod,
                          List<OrderItemDTO> itemsDTO,
                          BigDecimal freightValue,
                          BigDecimal totalPrice) {

        if (itemsDTO == null || itemsDTO.isEmpty()) {
            throw new IllegalStateException("Carrinho vazio");
        }

        ClientAddress address = clientAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + addressId));

        Order order = new Order();
        order.setClient(client);
        order.setAddress(address);
        order.setPaymentDetails(paymentDetails);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
        order.setFreightValue(freightValue);
        order.setTotalPrice(totalPrice);
        order.setPaymentMethod(paymentMethod);

        order = orderRepository.save(order);

        for (OrderItemDTO itemDTO : itemsDTO) {
            Product product = new Product();
            product.setId(Long.parseLong(itemDTO.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(itemDTO.getUnitPrice());
            orderItem.setTotalPrice(itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            orderItemRepository.save(orderItem);
        }

        return order;
    }



}
