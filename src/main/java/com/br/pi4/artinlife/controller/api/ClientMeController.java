package com.br.pi4.artinlife.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientMeController {

    @GetMapping("/me")
    public ResponseEntity<?> getClientInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(Map.of("email", principal.getName()));
    }
}
