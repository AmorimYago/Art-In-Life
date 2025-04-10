package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    private final AppUserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserInfo(Principal principal) {
        AppUser user = userRepository.findByEmail(principal.getName()).orElseThrow();
        Map<String, Object> info = new HashMap<>();
        info.put("email", user.getEmail());
        info.put("type", user.getType().name()); // Ex: "ADMIN", "STOCKER"
        return ResponseEntity.ok(info);
    }
}
