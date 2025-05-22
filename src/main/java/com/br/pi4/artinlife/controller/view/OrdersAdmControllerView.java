package com.br.pi4.artinlife.controller.view;

import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.model.OrderItem;
import com.br.pi4.artinlife.model.OrderStatus;
import com.br.pi4.artinlife.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/pedidos") // agora sem conflito com a API
@RequiredArgsConstructor
public class OrdersAdmControllerView {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getOrdersSortedByDate();
        model.addAttribute("orders", orders);
        return "admin/ordersadm";
    }

    @GetMapping("/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        List<OrderItem> items = orderService.getOrderItemsByOrder(order);
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        return "admin/order-detail";
    }

    @PostMapping("/{id}/status-update")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") OrderStatus status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/pedidos/" + id;
    }
}
