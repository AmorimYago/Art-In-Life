package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientPageControllerView {

    @GetMapping("/client/profile")
    public String client() {
        return "client/clientpage";
    }

}
