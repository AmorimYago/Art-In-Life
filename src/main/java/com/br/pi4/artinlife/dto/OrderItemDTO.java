package com.br.pi4.artinlife.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private String productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
