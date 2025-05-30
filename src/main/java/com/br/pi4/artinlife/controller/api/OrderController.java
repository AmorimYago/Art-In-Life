package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.request.CheckoutRequest;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.model.OrderItem;
import com.br.pi4.artinlife.repository.ClientAddressRepository;
import com.br.pi4.artinlife.service.ClientService;
import com.br.pi4.artinlife.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private ClientAddressRepository clientAddressRepository;
    private final OrderService orderService;
    @Autowired
    private ClientService clientService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestBody CheckoutRequest checkoutRequest) {
        Client client = clientService.getClientById(checkoutRequest.getClientId());
        Order order = orderService.checkout(
                client,
                Long.parseLong(checkoutRequest.getAddressId()),
                checkoutRequest.getPaymentDetails(),
                checkoutRequest.getPaymentMethod(),
                checkoutRequest.getItems(),
                checkoutRequest.getFreightValue(),
                checkoutRequest.getTotalPrice()
        );
        return ResponseEntity.ok(order);
    }


    @GetMapping
    public ResponseEntity<List<Order>> getOrders(Principal principal) {
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

        return ResponseEntity.ok(orders);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        List<OrderItem> orderItems = orderService.getOrderItemsByOrder(order);
        return ResponseEntity.ok(orderItems);
    }
}
