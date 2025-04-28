package com.br.pi4.artinlife.config;

import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.model.UserType;
import com.br.pi4.artinlife.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Bean
    CommandLineRunner initUsers(AppUserRepository repository, PasswordEncoder encoder) {
        return args -> {

            if (repository.findByEmail("admin@email.com").isEmpty()) {
                repository.save(AppUser.builder()
                        .name("Administrador")
                        .email("admin@email.com")
                        .cpf("49458588880")
                        .password(encoder.encode("123"))
                        .status(true)
                        .type(UserType.ADMIN)
                        .build()
                );
            }

            if (repository.findByEmail("stocker@email.com").isEmpty()) {
                repository.save(AppUser.builder()
                        .name("Estoquista")
                        .email("stocker@email.com")
                        .cpf("51453264086")
                        .password(encoder.encode("123"))
                        .status(true)
                        .type(UserType.STOCKER)
                        .build()
                );
            }
        };
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer executado");
    }
}
