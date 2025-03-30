package com.br.pi4.artinlife.controller.view.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersAdmController {

    @GetMapping("/admin/usersadm")
    public String usersAdm() {
        return "admin/usersadm";
    }

}
