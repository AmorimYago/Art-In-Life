package com.br.pi4.artinlife.dto.request;

import com.br.pi4.artinlife.dto.OrderItemDTO;
import com.br.pi4.artinlife.model.ClientAddress;
import com.br.pi4.artinlife.model.PaymentDetails;
import com.br.pi4.artinlife.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    private String addressId;
    private PaymentDetails paymentDetails;
    private PaymentMethod paymentMethod;
    private List<OrderItemDTO> items;
    private BigDecimal freightValue;
    private BigDecimal totalPrice;
    private Long clientId;

}