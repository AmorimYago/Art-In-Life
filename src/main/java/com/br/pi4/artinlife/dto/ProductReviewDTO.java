package com.br.pi4.artinlife.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductReviewDTO {

    @NotBlank
    private String productId;

    @NotBlank
    private String clientId;

    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}
