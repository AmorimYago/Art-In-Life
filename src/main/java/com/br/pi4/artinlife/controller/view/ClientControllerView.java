package com.br.pi4.artinlife.controller.view;

import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Order;
import com.br.pi4.artinlife.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ClientControllerView {

    private final OrderService orderService;

    @GetMapping("/client/my-orders")
    public String viewClientOrders(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByClient(client);
        model.addAttribute("orders", orders);
        return "client/client-orders";
    }
}