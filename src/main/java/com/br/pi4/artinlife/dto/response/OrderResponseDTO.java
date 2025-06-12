package com.br.pi4.artinlife.dto.response;
import com.br.pi4.artinlife.model.ClientAddress;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderResponseDTO {
    private long id;
    private String paymentMethod;
    private ClientAddress address;
    private List<OrderItemResponseDTO> items;
    private LocalDateTime orderDate;
    private BigDecimal freightValue;
    private BigDecimal totalPrice;
    private String status;
}