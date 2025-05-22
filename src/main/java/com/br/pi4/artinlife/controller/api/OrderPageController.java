package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.OrderDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class OrderPageController {

    private final OrderService orderService;

    @GetMapping("/my-orders/{id}")
    public String showOrderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        AppUser user = (AppUser) authentication.getPrincipal();
        Order order = orderService.getOrderById(id);

        if (order == null || !order.getClient().getId().equals(user.getId())) {
            return "error/404";
        }

        model.addAttribute("order", order);
        return "client/order-detail";
    }
}