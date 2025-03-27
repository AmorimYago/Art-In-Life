package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    // Ex: buscar itens de um pedido específico
}
