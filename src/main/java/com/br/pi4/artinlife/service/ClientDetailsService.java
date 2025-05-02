package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ClientDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado com e-mail: " + email));

        return User.builder()
                .username(client.getEmail())
                .password(client.getPassword())
                .disabled(!client.getStatus())
                .roles("CLIENT") // Define a role padrão
                .build();
    }
}
