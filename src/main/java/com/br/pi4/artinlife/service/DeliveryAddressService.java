package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.ClientAddressDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.model.ClientAddress;
import com.br.pi4.artinlife.repository.AppUserRepository;
import com.br.pi4.artinlife.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository addressRepository;
    private final AppUserRepository userRepository;

    /**
     * Cadastra um novo endereço de entrega.
     */
    public ClientAddress register(ClientAddressDTO dto) {
        AppUser user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ClientAddress address = ClientAddress.builder()
                .user(user)
                .cep(dto.getCep())
                .street(dto.getStreet())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .neighborhood(dto.getNeighborhood())
                .city(dto.getCity())
                .state(dto.getState())
                .build();

        return addressRepository.save(address);
    }
}
