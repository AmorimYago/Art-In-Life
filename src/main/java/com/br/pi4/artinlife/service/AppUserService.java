package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.AppUserDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    public boolean cpfExists(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Transactional
    public AppUser create(AppUserDTO dto) {
        AppUser user = AppUser.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .type(dto.getType())
                .status(true)
                .build();

        return repository.save(user);
    }

    @Transactional
    public AppUser update(String id, AppUserDTO dto) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setName(dto.getName());
        user.setCpf(dto.getCpf());
        user.setEmail(dto.getEmail());
        user.setType(dto.getType());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return repository.save(user);
    }

    public AppUser setStatus(String id, boolean status) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setStatus(status);
        return repository.save(user);
    }

    public Optional<AppUser> findById(String id) {
        return repository.findById(id);
    }

    public List<AppUser> findAll() {
        return repository.findAll();
    }
}
