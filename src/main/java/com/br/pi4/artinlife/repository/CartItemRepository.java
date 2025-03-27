package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    // Ex: buscar itens de um carrinho específico
}
