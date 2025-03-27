package com.br.pi4.artinlife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {

    @NotBlank
    private String productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private BigDecimal unitPrice; // opcional na entrada, mas usado na resposta
}
