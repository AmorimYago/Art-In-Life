package com.br.pi4.artinlife.dto;

import com.br.pi4.artinlife.model.UserType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AppUserDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String cpf;

    @Email
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @NotNull
    private UserType type;
}
