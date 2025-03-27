package com.br.pi4.artinlife.dto;

import com.br.pi4.artinlife.model.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate birthDate;

    @NotNull(message = "Gênero é obrigatório")
    private Gender gender;
}