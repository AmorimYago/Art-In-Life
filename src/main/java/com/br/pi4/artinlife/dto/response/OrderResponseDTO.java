package com.br.pi4.artinlife.dto.response; // Crie um pacote para DTOs de resposta

import com.br.pi4.artinlife.model.ClientAddress; // Pode usar diretamente se não tiver problemas de serialização
import com.br.pi4.artinlife.model.PaymentDetails; // Pode ser um DTO de resposta se necessário
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderResponseDTO {
    private long id;
    private String paymentMethod; // Ou PaymentMethod como enum, se preferir
    private ClientAddress address; // Ou ClientAddressResponseDTO, se address tiver LAZYs
    private List<OrderItemResponseDTO> items; // Lista de OrderItemResponseDTO
    private LocalDateTime orderDate;
    private BigDecimal freightValue;
    private BigDecimal totalPrice;
    private String status; // Ou OrderStatus como enum
}