package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClient(Client client);
}
