package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientAddressRepository extends JpaRepository<ClientAddress, Long> {
    List<ClientAddress> findByClientId(Long clientId);
    List<ClientAddress> findByClientIdAndDefaultDeliveryAddressTrue(Long clientId);
}