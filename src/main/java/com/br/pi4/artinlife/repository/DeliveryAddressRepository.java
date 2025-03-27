package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, String> {
    List<DeliveryAddress> findByUserId(String userId);
}
