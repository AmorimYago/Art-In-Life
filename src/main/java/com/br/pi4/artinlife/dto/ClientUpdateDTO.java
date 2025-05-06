package com.br.pi4.artinlife.dto;

import com.br.pi4.artinlife.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientUpdateDTO {
    @NotBlank
    @Pattern(regexp = "^(?=.{1,40}$)[A-Za-zÀ-ÿ]+(?:\\s[A-Za-zÀ-ÿ]+)+$", message = "O nome deve conter pelo menos duas palavras com letras válidas")
    private String fullName;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private Gender gender;

    private String password; // opcional
}
