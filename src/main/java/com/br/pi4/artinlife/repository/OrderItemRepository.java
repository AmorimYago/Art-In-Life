package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    List<OrderItem> findByOrder(Order order);
    // Ex: buscar itens de um pedido específico
}
