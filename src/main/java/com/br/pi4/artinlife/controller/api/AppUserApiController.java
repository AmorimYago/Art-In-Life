package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.AppUserDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class AppUserApiController {

    @Autowired
    private AppUserService userService;

    @GetMapping
    public List<AppUser> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getById(@PathVariable String id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AppUser> create(@RequestBody @Valid AppUserDTO dto) {
        AppUser created = userService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> update(@PathVariable String id, @RequestBody @Valid AppUserDTO dto) {
        AppUser updated = userService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<AppUser> disable(@PathVariable String id) {
        AppUser updated = userService.setStatus(id, false);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<AppUser> enable(@PathVariable String id) {
        AppUser updated = userService.setStatus(id, true);
        return ResponseEntity.ok(updated);
    }
}
