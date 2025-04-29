package com.br.pi4.artinlife.dto;

import com.br.pi4.artinlife.model.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    @Pattern(regexp = "^(?=.{1,40}$)[A-Za-zÀ-ÿ]+(?:\\s[A-Za-zÀ-ÿ]+)+$", message = "O nome deve conter pelo menos duas palavras com letras válidas")
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

    @NotNull(message = "O endereço de faturamento é obrigatório")
    @Valid
    private AddressDTO billingAddress;

    @NotNull(message = "O endereço de entrega é obrigatório")
    @Valid
    private AddressDTO deliveryAddress;

    private List<@Valid AddressDTO> additionalDeliveryAddresses;
}