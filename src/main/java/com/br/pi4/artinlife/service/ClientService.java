package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.ClientDTO;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Client create(ClientDTO dto) {
        Client client = Client.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .status(true) // ✅ garante que sempre venha com status ativo
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return clientRepository.save(client);
    }

    @Transactional
    public Client update(String id, ClientDTO dto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        client.setFullName(dto.getFullName());
        client.setEmail(dto.getEmail());
        client.setCpf(dto.getCpf());
        client.setBirthDate(dto.getBirthDate());
        client.setGender(dto.getGender());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            client.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return clientRepository.save(client);
    }
}
