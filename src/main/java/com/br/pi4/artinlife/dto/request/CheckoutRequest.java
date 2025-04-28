package com.br.pi4.artinlife.dto.request;

import com.br.pi4.artinlife.model.Address;
import com.br.pi4.artinlife.model.PaymentDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    private Address address;
    private PaymentDetails paymentDetails;
}
