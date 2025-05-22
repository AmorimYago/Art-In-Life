package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.model.OrderStatus;
import com.br.pi4.artinlife.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderAdmController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getOrdersSortedByDate();
        model.addAttribute("orders", orders);
        return "admin/ordersadm";
    }

    //@PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            try {
                order.setStatus(OrderStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                // log or ignore invalid status
            }
        }
        return "redirect:/admin/orders";
    }
}