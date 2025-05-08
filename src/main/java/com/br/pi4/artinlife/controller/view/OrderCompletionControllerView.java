package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderCompletionControllerView {

    @GetMapping("/client/order-confirmation")
    public String orderCompletion() {
        return "/client/order-completion";
    }

}
