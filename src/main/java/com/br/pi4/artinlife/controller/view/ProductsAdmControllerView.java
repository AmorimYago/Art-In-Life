package com.br.pi4.artinlife.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductsAdmControllerView {

    @GetMapping("/admin/productsadm")
    public String productsAdm() {
        return "admin/productsadm";
    }

}
