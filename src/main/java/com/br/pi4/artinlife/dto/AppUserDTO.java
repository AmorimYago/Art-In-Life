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

    @Size(min = 9, message = "Senha deve conter no minimo 8 caracteres")
    private String password;

    @NotNull
    private UserType type;
}
