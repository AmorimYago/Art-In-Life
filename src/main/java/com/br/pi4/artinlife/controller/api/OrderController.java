package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.request.CheckoutRequest;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.model.OrderItem;
import com.br.pi4.artinlife.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@AuthenticationPrincipal Client client,
                                          @RequestBody CheckoutRequest checkoutRequest) {
        Order order = orderService.checkout(client, checkoutRequest.getAddress(), checkoutRequest.getPaymentDetails());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@AuthenticationPrincipal Client client) {
        List<Order> orders = orderService.getOrdersByClient(client);
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
