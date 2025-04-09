package com.br.pi4.artinlife.controller.dev;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DevUtilsController {

    @GetMapping("/encode")
    public String encodePassword(@RequestParam String senha) {
        return new BCryptPasswordEncoder().encode(senha);
    }

}
