package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}