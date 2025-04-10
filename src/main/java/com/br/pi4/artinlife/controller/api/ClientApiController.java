package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.ClientDTO;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
