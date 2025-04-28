package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome é obrigatório")
    @Pattern(regexp = "^(?=.{7,})([A-Za-zÀ-ÿ]{3,}\\s[A-Za-zÀ-ÿ]{3,})$", message = "O nome deve conter pelo menos duas palavras com no mínimo 3 letras cada")
    private String fullName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @Column(nullable = false)
    @NotBlank(message = "A senha é obrigatória")
    private String password;

    @Column(nullable = false)
    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate birthDate;

    @Column(nullable = false)
    @NotNull(message = "O gênero é obrigatório")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private Boolean status = true;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientAddress> addresses;
}
