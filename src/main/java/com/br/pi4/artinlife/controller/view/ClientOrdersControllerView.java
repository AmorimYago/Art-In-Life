package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientOrdersControllerView {

    @GetMapping("/client-orders")
    public String clientOrders() {
        return "client/client-orders";
    }

}
