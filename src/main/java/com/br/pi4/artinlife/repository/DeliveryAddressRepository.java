package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAddressRepository extends JpaRepository<ClientAddress, String> {
    List<ClientAddress> findByUserId(String userId);
}
