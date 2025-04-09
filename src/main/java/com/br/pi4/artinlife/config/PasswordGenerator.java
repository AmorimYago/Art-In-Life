package com.br.pi4.artinlife.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordGenerator {

    /*
    @Bean
    public CommandLineRunner generatePasswordHash(BCryptPasswordEncoder encoder) {
        return args -> {
            String rawPassword = "admin1";
            String encoded = encoder.encode(rawPassword);
            System.out.println("🔐 Hash gerado para 'admin1': " + encoded);
        };
    }
     */

    /*
     @Autowired
    private BCryptPasswordEncoder encoder;

    @PostConstruct
    public void testPassword() {
        String raw = "admin1";
        String hashed = "$2a$10$R9xQbdVwrnVKg6F/v1Q7AepOdkBkWTshKopHg6uKXitAj8OH0K0rq";
        boolean match = encoder.matches(raw, hashed);
        System.out.println("✔️ Comparação de senha: " + match);
    }
     */

    /*
    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostConstruct
    public void generateHash() {
        String raw = "admin1";
        String encoded = encoder.encode(raw);
        System.out.println("🧂 Hash da senha: " + encoded);
    }
     */

}
