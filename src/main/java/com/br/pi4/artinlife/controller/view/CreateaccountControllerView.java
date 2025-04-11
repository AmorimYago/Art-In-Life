package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreateaccountControllerView {

    @GetMapping("/create-account")
    public String createAccount() {
        return "public/createaccount";
    }

}
