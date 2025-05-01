package com.br.pi4.artinlife.config;

import com.br.pi4.artinlife.model.*;
import com.br.pi4.artinlife.repository.AppUserRepository;
import com.br.pi4.artinlife.repository.ClientRepository;
import com.br.pi4.artinlife.repository.ProductRepository;
import com.br.pi4.artinlife.service.ClientService;
import com.br.pi4.artinlife.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer executado");
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {

            // Criar usuários ADMIN e STOCKER
            if (appUserRepository.findByEmail("admin@email.com").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .name("Administrador")
                        .email("admin@email.com")
                        .cpf("49458588880")
                        .password(passwordEncoder.encode("123"))
                        .status(true)
                        .type(UserType.ADMIN)
                        .build()
                );
            }

            if (appUserRepository.findByEmail("stocker@email.com").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .name("Estoquista")
                        .email("stocker@email.com")
                        .cpf("51453264086")
                        .password(passwordEncoder.encode("123"))
                        .status(true)
                        .type(UserType.STOCKER)
                        .build()
                );
            }

            // Criação de dois CLIENTES
            if (clientRepository.findByEmail("joao.silva@example.com").isEmpty()) {
                Client client1 = Client.builder()
                        .fullName("João da Silva")
                        .email("joao.silva@example.com")
                        .cpf("60186699000") // CPF válido corrigido
                        .password(passwordEncoder.encode("senha123"))
                        .birthDate(LocalDate.of(1990, 5, 15))
                        .gender(Gender.MALE)
                        .status(true)
                        .build();

                clientRepository.save(client1);
            }

            if (clientRepository.findByEmail("maria.oliveira@example.com").isEmpty()) {
                Client client2 = Client.builder()
                        .fullName("Maria Oliveira")
                        .email("maria.oliveira@example.com")
                        .cpf("02003099000") // CPF válido corrigido
                        .password(passwordEncoder.encode("senha123"))
                        .birthDate(LocalDate.of(1995, 8, 22))
                        .gender(Gender.FEMALE)
                        .status(true)
                        .build();

                clientRepository.save(client2);
            }

            // Criação de três PRODUTOS
            if (productRepository.findAll().isEmpty()) {
                Product product1 = Product.builder()
                        .name("Quadro Knight Grito do Abismo – Hollow Knight")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(10)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product2 = Product.builder()
                        .name("Quadro Eileen – Bloodborne")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(5)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product3 = Product.builder()
                        .name("Quadro Totoro na Noite Estrelada")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                product1 = productRepository.save(product1);
                product2 = productRepository.save(product2);
                product3 = productRepository.save(product3);

                productService.addProductImage(product1.getId(), "quadro_knight_grito_do_abismo_hollow_knight.png", true);
                productService.addProductImage(product2.getId(), "quadro_eileen_bloodborne.png", true);
                productService.addProductImage(product3.getId(), "quadro_totoro_na_noite_estrelada.png", true);
            }
        };
    }
}
