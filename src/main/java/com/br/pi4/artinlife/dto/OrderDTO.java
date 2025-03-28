package com.br.pi4.artinlife.dto;

import com.br.pi4.artinlife.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {

    private String clientId;
    private String addressId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderItemDTO> items;
}
