package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.AppUserDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService implements UserDetailsService {

    @Autowired
    private AppUserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getType().name())
                .build();
    }

    @Transactional
    public AppUser create(AppUserDTO dto) {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(true)
                .type(dto.getType())
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
        user.setPassword(dto.getPassword());
        user.setType(dto.getType());

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
