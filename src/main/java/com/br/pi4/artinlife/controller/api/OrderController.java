package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.request.CheckoutRequest;
import com.br.pi4.artinlife.dto.response.OrderResponseDTO; // Importe o DTO de resposta
import com.br.pi4.artinlife.dto.response.OrderItemResponseDTO; // Importe o DTO de resposta
import com.br.pi4.artinlife.dto.response.ProductResponseDTO; // Importe o DTO de resposta
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.model.OrderItem;
import com.br.pi4.artinlife.service.ClientService;
import com.br.pi4.artinlife.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    // @Autowired ClientAddressRepository não é mais necessário aqui se o serviço cuida disso
    private final OrderService orderService;
    private final ClientService clientService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        Client client = clientService.getClientById(checkoutRequest.getClientId());
        Order order = orderService.checkout(
                client,
                Long.parseLong(checkoutRequest.getAddressId()),
                checkoutRequest.getPaymentDetails(),
                checkoutRequest.getPaymentMethod(),
                checkoutRequest.getItems(), // DTO de requisição aqui
                checkoutRequest.getFreightValue(),
                checkoutRequest.getTotalPrice()
        );
        return ResponseEntity.ok(order); // Retorna a entidade Order no checkout
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(Principal principal) {
        if (principal == null) {
            System.out.println("Principal é null");
            return ResponseEntity.status(401).build();
        }

        String email = principal.getName();
        System.out.println("Email autenticado: " + email);

        Client client = clientService.getClientByEmail(email);
        if (client == null) {
            System.out.println("Cliente não encontrado com o email: " + email);
            return ResponseEntity.status(404).build();
        }

        List<Order> orders = orderService.getOrdersByClient(client);
        System.out.println("Total de pedidos: " + orders.size());

        // Mapear a lista de entidades Order para OrderResponseDTOs
        List<OrderResponseDTO> orderDTOs = orders.stream()
                .map(order -> {
                    OrderResponseDTO dto = new OrderResponseDTO();
                    dto.setId(order.getId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setTotalPrice(order.getTotalPrice());
                    dto.setStatus(order.getStatus().name());
                    // Mapeie outros campos conforme necessário, mas tenha cuidado com LAZYs se não for um getOrderById transacional
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long orderId) {
        // 1. Busca a entidade Order do serviço (ela já terá os LAZYs inicializados)
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Mapeia a entidade Order para o OrderResponseDTO
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(order.getId());
        orderResponseDTO.setPaymentMethod(order.getPaymentMethod().name());
        orderResponseDTO.setOrderDate(order.getOrderDate());
        orderResponseDTO.setFreightValue(order.getFreightValue());
        orderResponseDTO.setTotalPrice(order.getTotalPrice());
        orderResponseDTO.setStatus(order.getStatus().name());

        // Mapear endereço (ClientAddress)
        // Se ClientAddress tiver campos LAZY, você precisaria de um ClientAddressResponseDTO aqui.
        orderResponseDTO.setAddress(order.getAddress());


        // Mapear itens e produtos (este é o ponto crucial para evitar o proxy no frontend)
        if (order.getItems() != null) {
            List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                    .map(item -> {
                        OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                        itemDTO.setId(item.getId());
                        itemDTO.setQuantity(item.getQuantity());
                        itemDTO.setUnitPrice(item.getUnitPrice());
                        itemDTO.setTotalPrice(item.getTotalPrice()); // Use o totalPrice do OrderItem

                        // Mapeia o Product para ProductResponseDTO
                        if (item.getProduct() != null) {
                            itemDTO.setProduct(new ProductResponseDTO(item.getProduct())); // Usa o construtor do DTO
                        }
                        return itemDTO;
                    })
                    .collect(Collectors.toList());
            orderResponseDTO.setItems(itemDTOs);
        }

        return ResponseEntity.ok(orderResponseDTO);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        // Este endpoint separado para itens pode ser mantido,
        // mas se getOrder já retorna os itens, este pode ser redundante.
        Order order = orderService.getOrderById(orderId); // getOrderById já carrega os itens
        List<OrderItem> orderItems = orderService.getOrderItemsByOrder(order);
        return ResponseEntity.ok(orderItems);
    }
}