package com.br.pi4.artinlife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    // Avaliação (de 1 a 5 estrelas)
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5")
    private Float rating;

    // Lista de caminhos de imagem que o usuário pode enviar na criação (opcional)
    private List<String> imagePaths;

    // Qual imagem deve ser marcada como principal (opcional)
    private String mainImagePath;
}
