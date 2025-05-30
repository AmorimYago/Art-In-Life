package com.br.pi4.artinlife.dto.response; // Crie um pacote para DTOs de resposta

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private ProductResponseDTO product; // Agora usa o DTO de resposta do produto
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice; // O total do item (unitPrice * quantity)
}