package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.AddressDTO;
import com.br.pi4.artinlife.dto.ClientDTO;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.ClientAddress;
import com.br.pi4.artinlife.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientApiController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody @Valid ClientDTO dto) {
        Client created = clientService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable String id, @RequestBody @Valid ClientDTO dto) {
        Client updated = clientService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String id, @RequestBody String newPassword) {
        clientService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<ClientAddress> addDeliveryAddress(@PathVariable String id, @RequestBody @Valid AddressDTO addressDTO) {
        ClientAddress address = clientService.addDeliveryAddress(id, addressDTO);
        return ResponseEntity.status(201).body(address);
    }

    @PutMapping("/{id}/addresses/{addressId}/default")
    public ResponseEntity<Void> setDefaultDeliveryAddress(@PathVariable String id, @PathVariable String addressId) {
        clientService.setDefaultDeliveryAddress(id, addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clients/{clientId}/addresses")
    public ResponseEntity<List<ClientAddress>> getClientAddresses(@PathVariable Long clientId) {
        List<ClientAddress> addresses = clientService.getAddressesByClientId(clientId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/clients/{clientId}/addresses/main")
    public ResponseEntity<List<ClientAddress>> getClientMainAddresses(@PathVariable Long clientId) {
        List<ClientAddress> mainAddresses = clientService.getMainAddressesByClientId(clientId);
        return ResponseEntity.ok(mainAddresses);
    }
}
