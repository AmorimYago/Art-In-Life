package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.AddressDTO;
import com.br.pi4.artinlife.dto.ClientDTO;
import com.br.pi4.artinlife.exception.ResourceNotFoundException;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.ClientAddress;
import com.br.pi4.artinlife.repository.ClientAddressRepository;
import com.br.pi4.artinlife.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientAddressRepository clientAddressRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Client create(ClientDTO dto) {
        Client client = Client.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .status(true)
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        client = clientRepository.save(client);

        List<ClientAddress> addresses = new ArrayList<>();

        ClientAddress billing = toAddress(dto.getBillingAddress(), client);
        billing.setBillingAddress(true);
        addresses.add(billing);

        ClientAddress delivery = toAddress(dto.getDeliveryAddress(), client);
        delivery.setDefaultDeliveryAddress(true);
        addresses.add(delivery);

        if (dto.getAdditionalDeliveryAddresses() != null) {
            for (AddressDTO extra : dto.getAdditionalDeliveryAddresses()) {
                ClientAddress address = toAddress(extra, client);
                // Os adicionais não são nem billing nem principal por padrão
                addresses.add(address);
            }
        }

        client.setAddresses(addresses);
        return clientRepository.save(client);
    }

    private ClientAddress toAddress(AddressDTO dto, Client client) {
        return ClientAddress.builder()
                .client(client)
                .cep(dto.getCep())
                .street(dto.getStreet())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .neighborhood(dto.getNeighborhood())
                .city(dto.getCity())
                .state(dto.getState())
                .billingAddress(dto.isBillingAddress())
                .defaultDeliveryAddress(dto.isDefaultDeliveryAddress())
                .build();
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

    @Transactional
    public void changePassword(String clientId, String newPassword) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        client.setPassword(passwordEncoder.encode(newPassword));
        clientRepository.save(client);
    }

    @Transactional
    public ClientAddress addDeliveryAddress(String clientId, AddressDTO addressDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        if (addressDTO.isDefaultDeliveryAddress()) {
            client.getAddresses().forEach(addr -> addr.setDefaultDeliveryAddress(false));
        }

        ClientAddress address = ClientAddress.builder()
                .client(client)
                .cep(addressDTO.getCep())
                .street(addressDTO.getStreet())
                .number(addressDTO.getNumber())
                .complement(addressDTO.getComplement())
                .neighborhood(addressDTO.getNeighborhood())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .billingAddress(addressDTO.isBillingAddress())
                .defaultDeliveryAddress(addressDTO.isDefaultDeliveryAddress())
                .build();

        return clientAddressRepository.save(address);
    }


    @Transactional
    public void setDefaultDeliveryAddress(String clientId, String addressId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        client.getAddresses().forEach(addr -> addr.setDefaultDeliveryAddress(false));

        ClientAddress address = client.getAddresses().stream()
                .filter(addr -> addr.getId().equals(Long.valueOf(addressId)))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        address.setDefaultDeliveryAddress(true);
        clientAddressRepository.save(address);
    }

    /**
     * Retorna todos os endereços associados a um cliente.
     *
     * @param clientId ID do cliente
     * @return Lista de endereços do cliente
     */
    public List<ClientAddress> getAddressesByClientId(Long clientId) {
        return clientAddressRepository.findByClientId(clientId);
    }

    public List<ClientAddress> getMainAddressesByClientId(Long clientId) {
        return clientAddressRepository.findByClientIdAndDefaultDeliveryAddressTrue(clientId);
    }

    /**
     * Retorna o endereço principal (main) de um cliente.
     *
     * @param clientId ID do cliente
     * @return Endereço principal do cliente
     * @throws ResourceNotFoundException se o endereço principal não for encontrado
     */
    public ClientAddress getDefaultDeliveryAddressByClientId(Long clientId) {
        return clientAddressRepository.findByClientIdAndDefaultDeliveryAddressTrue(clientId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Endereço principal não encontrado para o cliente com ID: " + clientId));
    }
}
