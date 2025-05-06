package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.AddressDTO;
import com.br.pi4.artinlife.dto.ClientDTO;
import com.br.pi4.artinlife.dto.ClientUpdateDTO;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.ClientAddress;
import com.br.pi4.artinlife.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientApiController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody ClientDTO dto) {
        Client created = clientService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable String email) {
        Client client = clientService.getClientByEmail(email);
        return ResponseEntity.ok(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id,
                                         @Valid @RequestBody ClientUpdateDTO dto) {
        Client updated = clientService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @RequestBody String newPassword) {
        clientService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<ClientAddress> addDeliveryAddress(@PathVariable Long id,
                                                            @Valid @RequestBody AddressDTO addressDTO) {
        ClientAddress address = clientService.addDeliveryAddress(id, addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @PutMapping("/{id}/addresses/{addressId}/default")
    public ResponseEntity<Void> setDefaultDeliveryAddress(@PathVariable Long id,
                                                          @PathVariable String addressId) {
        clientService.setDefaultDeliveryAddress(id, addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<ClientAddress>> getClientAddresses(@PathVariable Long id) {
        List<ClientAddress> addresses = clientService.getAddressesByClientId(id);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}/addresses/main")
    public ResponseEntity<List<ClientAddress>> getClientMainAddresses(@PathVariable Long id) {
        List<ClientAddress> mainAddresses = clientService.getMainAddressesByClientId(id);
        return ResponseEntity.ok(mainAddresses);
    }

    @GetMapping("/{id}/addresses/default")
    public ResponseEntity<ClientAddress> getDefaultDeliveryAddress(@PathVariable Long id) {
        ClientAddress defaultAddress = clientService.getDefaultDeliveryAddressByClientId(id);
        return ResponseEntity.ok(defaultAddress);
    }
}
