package com.br.pi4.artinlife.dto;

import com.br.pi4.artinlife.model.CartStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {

    private String clientId; // pode ser null (anônimo)
    private BigDecimal totalPrice;
    private CartStatus status;
    private List<CartItemDTO> items;
}
