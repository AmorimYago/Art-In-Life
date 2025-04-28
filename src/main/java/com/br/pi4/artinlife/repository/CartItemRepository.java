package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Cart;
import com.br.pi4.artinlife.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);
    // Ex: buscar itens de um carrinho específico
}
