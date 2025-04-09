package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersAdmControllerView {

    @GetMapping("/admin/usersadm")
    public String usersAdm() {
        return "admin/usersadm";
    }

}
