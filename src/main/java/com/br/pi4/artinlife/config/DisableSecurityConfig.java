/*
package com.br.pi4.artinlife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DisableSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desativa proteção CSRF (evita erro em POST sem token)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Libera acesso a todas as rotas
                )
                .formLogin(form -> form.disable()) // Remove tela de login
                .httpBasic(basic -> basic.disable()); // Remove login por popup

        return http.build();
    }
}

 */