package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.model.ClientAddress;
import com.br.pi4.artinlife.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients/{clientId}/addresses")
@RequiredArgsConstructor
public class ClientAddressApiController {

    private final ClientService clientService;

    /**
     * Retorna todos os endereços de um cliente.
     *
     * @param clientId ID do cliente
     * @return Lista de endereços
     */
    @GetMapping
    public ResponseEntity<List<ClientAddress>> getAllAddresses(@PathVariable Long clientId) {
        List<ClientAddress> addresses = clientService.getAddressesByClientId(clientId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Retorna o endereço principal de um cliente.
     *
     * @param clientId ID do cliente
     * @return Endereço principal
     */
    @GetMapping("/main")
    public ResponseEntity<ClientAddress> getMainAddress(@PathVariable Long clientId) {
        ClientAddress mainAddress = clientService.getDefaultDeliveryAddressByClientId(clientId);
        return ResponseEntity.ok(mainAddress);
    }

    @GetMapping("/default-address")
    public ResponseEntity<ClientAddress> getDefaultDeliveryAddress(@PathVariable Long clientId) {
        ClientAddress address = clientService.getDefaultDeliveryAddressByClientId(clientId);
        return ResponseEntity.ok(address);
    }
}
