package com.br.pi4.artinlife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClientAddressDTO {

    @NotBlank
    private String clientId;

    @NotBlank
    private String cep;

    @NotBlank
    private String street;

    @NotBlank
    private String number;

    private String complement;

    @NotBlank
    private String neighborhood;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    private boolean main; // true se for o endereço principal
}
