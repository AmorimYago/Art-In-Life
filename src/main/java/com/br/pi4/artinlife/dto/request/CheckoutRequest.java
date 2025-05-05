package com.br.pi4.artinlife.dto.request;

import com.br.pi4.artinlife.model.ClientAddress;
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
    private ClientAddress address;
    private PaymentDetails paymentDetails;
}
