package com.br.pi4.artinlife.controller.view;

import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;

    @GetMapping("/products")
    public String searchProducts(@RequestParam(required = false) String search, Model model) {
        List<Product> produtos = productService.searchActiveProductsByName(search);
        model.addAttribute("produtos", produtos);
        return "public/store";
    }

    @GetMapping("/products/category/{id}")
    public String filterByCategory(@PathVariable Long id, Model model) {
        List<Product> produtos = productService.findByCategoryId(id);
        model.addAttribute("produtos", produtos);
        return "public/store";
    }
}
