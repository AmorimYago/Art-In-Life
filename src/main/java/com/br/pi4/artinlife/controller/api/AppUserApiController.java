package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.AppUserDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserApiController {

    private final AppUserService appUserService;

    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = appUserService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/cpf")
    public ResponseEntity<Boolean> checkCpfExists(@RequestParam String cpf) {
        boolean exists = appUserService.cpfExists(cpf);
        return ResponseEntity.ok(exists);
    }

    @Autowired
    private AppUserService service;

    // Criar usuário
    @PostMapping
    public ResponseEntity<AppUser> create(@RequestBody AppUserDTO dto) {
        AppUser createdUser = service.create(dto);
        return ResponseEntity.ok(createdUser);
    }

    // Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<AppUser> update(@PathVariable String id, @RequestBody AppUserDTO dto) {
        AppUser updatedUser = service.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    // Alterar status do usuário (ativar/desativar)
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppUser> updateStatus(@PathVariable String id, @RequestParam boolean status) {
        AppUser user = service.setStatus(id, status);
        return ResponseEntity.ok(user);
    }

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<AppUser> findById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todos os usuários
    @GetMapping
    public ResponseEntity<List<AppUser>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
