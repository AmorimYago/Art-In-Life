package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Cart;
import com.br.pi4.artinlife.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByClient(Client client);
    // Ex: buscar carrinho aberto por usuário (opcional)
}
