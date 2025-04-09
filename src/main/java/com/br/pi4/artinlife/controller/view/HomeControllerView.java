package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerView {

    @GetMapping({"/", "/index", "/home"})
    public String home() {
        return "public/index";
    }

}