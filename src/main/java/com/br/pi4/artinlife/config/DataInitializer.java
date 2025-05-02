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
                        .name("Quadro Lady Maria – Bloodborne")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product4 = Product.builder()
                        .name("Quadro A Song of Crows – Bloodborne")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product5 = Product.builder()
                        .name("Quadro Totoro na Noite Estrelada")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product6 = Product.builder()
                        .name("Quadro Ponyo & Sosuke")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product7 = Product.builder()
                        .name("Quadro O Castelo Animado Minimalista")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product8 = Product.builder()
                        .name("Quadro A Viagem de Chihiro Minimalista")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product9 = Product.builder()
                        .name("Quadro Jiji")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product10 = Product.builder()
                        .name("Quadro Alphonse e Edward – Fullmetal Alchemist")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product11 = Product.builder()
                        .name("Quadro Pedra Filosofal – Fullmetal Alchemist")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product12 = Product.builder()
                        .name("Quadro Gojo Satoru – Jujutsu Kaisen")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product13 = Product.builder()
                        .name("Quadro Mahoraga e Megumi – Jujutsu Kaisen")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product14 = Product.builder()
                        .name("Quadro Kenshin Himura – Samurai X")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product15 = Product.builder()
                        .name("Quadro Jinx – Arcane")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product16 = Product.builder()
                        .name("Quadro Lua Vermelha – Red Dead Redemption")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product17 = Product.builder()
                        .name("Quadro Master Sword - Zelda")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product18 = Product.builder()
                        .name("Quadro Shadow of the Colossus")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product19 = Product.builder()
                        .name("Quadro Shadow of The Colossus End")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product20 = Product.builder()
                        .name("Quadro Desenho Malenia – Elden Ring")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product21 = Product.builder()
                        .name("Quadro Bonfire Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product22 = Product.builder()
                        .name("Quadro Silhueta Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product23 = Product.builder()
                        .name("Quadro Ornstein – Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product24 = Product.builder()
                        .name("Quadro Solaire – Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product25 = Product.builder()
                        .name("Quadro Gwyndolin – Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product26 = Product.builder()
                        .name("Quadro Artorias – Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();

                Product product27 = Product.builder()
                        .name("Quadro Abyss Watcher – Dark Souls")
                        .description("Quadro moderno com pintura abstrata em tons vibrantes.")
                        .price(new BigDecimal("29.90"))
                        .stock(8)
                        .status(true)
                        .rating(0.0f)
                        .build();


                product1 = productRepository.save(product1);
                product2 = productRepository.save(product2);
                product3 = productRepository.save(product3);
                product4 = productRepository.save(product4);
                product5 = productRepository.save(product5);
                product6 = productRepository.save(product6);
                product7 = productRepository.save(product7);
                product8 = productRepository.save(product8);
                product9 = productRepository.save(product9);
                product10 = productRepository.save(product10);
                product11 = productRepository.save(product11);
                product12 = productRepository.save(product12);
                product13 = productRepository.save(product13);
                product14 = productRepository.save(product14);
                product15 = productRepository.save(product15);
                product16 = productRepository.save(product16);
                product17 = productRepository.save(product17);
                product18 = productRepository.save(product18);
                product19 = productRepository.save(product19);
                product20 = productRepository.save(product20);
                product21 = productRepository.save(product21);
                product22 = productRepository.save(product22);
                product23 = productRepository.save(product23);
                product24 = productRepository.save(product24);
                product25 = productRepository.save(product25);
                product26 = productRepository.save(product26);
                product27 = productRepository.save(product27);


                productService.addProductImage(product1.getId(), "quadro_knight_grito_do_abismo_hollow_knight.png", true);
                productService.addProductImage(product2.getId(), "quadro_eileen_bloodborne.png", true);
                productService.addProductImage(product3.getId(), "quadro_lady_maria_bloodborne.png", true);
                productService.addProductImage(product4.getId(), "quadro_a_song_of_crows_bloodborne.png", true);
                productService.addProductImage(product5.getId(), "quadro_totoro_na_noite_estrelada.png", true);
                productService.addProductImage(product6.getId(), "quadro_ponyo_sosuke.png", true);
                productService.addProductImage(product7.getId(), "quadro_o_castelo_minimalista.png", true);
                productService.addProductImage(product8.getId(), "quadro_a_viagem_de_chihiro_minimalista.png", true);
                productService.addProductImage(product9.getId(), "quadro_jiji.png", true);
                productService.addProductImage(product10.getId(), "quadro_alphonse_edward_fullmetal_alchemist.png", true);
                productService.addProductImage(product11.getId(), "quadro_pedra_filosofal_fullmetal_alchemist.png", true);
                productService.addProductImage(product12.getId(), "quadro_gojo_satoru_jujutsu_kaisen.png", true);
                productService.addProductImage(product13.getId(), "quadro_mahoraga_megumi_jujutsu_kaisen.png", true);
                productService.addProductImage(product14.getId(), "quadro_kenshin_himura_samurai_x.png", true);
                productService.addProductImage(product15.getId(), "quadro_jinx_arcane.png", true);
                productService.addProductImage(product16.getId(), "quadro_lua_vermelha_red_dead_redemption.png", true);
                productService.addProductImage(product17.getId(), "quadro_master_sword_zelda.png", true);
                productService.addProductImage(product18.getId(), "quadro_shadow_of_the_colossus.png", true);
                productService.addProductImage(product19.getId(), "quadro_shadow_of_the_colossus_end.png", true);
                productService.addProductImage(product20.getId(), "quadro_desnenho_malenia_elden_ring.png", true);
                productService.addProductImage(product21.getId(), "quadro_bonfire_dark_souls.png", true);
                productService.addProductImage(product22.getId(), "quadro_silhueta_dark_souls.png", true);
                productService.addProductImage(product23.getId(), "quadro_ornstein_dark_souls.png", true);
                productService.addProductImage(product24.getId(), "quadro_solaire_dark_souls.png", true);
                productService.addProductImage(product25.getId(), "quadro_gwyndolin_dark_souls.png", true);
                productService.addProductImage(product26.getId(), "quadro_artorias_dark_souls.png", true);
                productService.addProductImage(product27.getId(), "quadro_abyss_watcher_dark_souls.png", true);
            }
        };
    }
}
