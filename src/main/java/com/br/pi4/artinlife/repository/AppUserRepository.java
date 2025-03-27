package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}