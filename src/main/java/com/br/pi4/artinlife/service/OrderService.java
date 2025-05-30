package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.OrderItemDTO;
import com.br.pi4.artinlife.exception.ResourceNotFoundException;
import com.br.pi4.artinlife.model.*;
import com.br.pi4.artinlife.repository.ClientAddressRepository;
import com.br.pi4.artinlife.repository.OrderItemRepository;
import com.br.pi4.artinlife.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
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

    @Transactional // Mantenha esta anotação
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + orderId));

        // Forçar a inicialização dos itens do pedido e seus produtos
        if (order.getItems() != null) {
            order.getItems().size(); // Força o carregamento da coleção de itens

            for (OrderItem item : order.getItems()) {
                // Acessa uma propriedade do Product para forçar a inicialização do proxy
                // Se Product tiver imagens LAZY, você precisaria inicializá-las aqui também.
                if (item.getProduct() != null) {
                    item.getProduct().getId(); // Ou item.getProduct().getName();
                    // Se Product.images for LAZY, inicialize também:
                    if (item.getProduct().getImages() != null) {
                        item.getProduct().getImages().size(); // Força o carregamento das imagens
                    }
                }
            }
        }
        // Se ClientAddress for LAZY e você precisar de todos os campos no DTO:
        if (order.getAddress() != null) {
            order.getAddress().getId(); // Ou qualquer getter para inicializar o proxy
        }

        return order;
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

    public List<Order> getOrdersSortedByDate() {
        List<Order> orders = orderRepository.findAll();
        orders.sort(Comparator.comparing(Order::getOrderDate).reversed());
        return orders;
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }


}
