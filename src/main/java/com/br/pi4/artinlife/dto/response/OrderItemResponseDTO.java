package com.br.pi4.artinlife.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}