package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, String> {
    // Ex: buscar carrinho aberto por usuário (opcional)
}
