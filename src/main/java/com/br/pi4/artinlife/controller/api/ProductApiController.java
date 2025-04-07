package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.ProductDTO;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid ProductDTO dto) {
        Product created = productService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody @Valid ProductDTO dto) {
        Product updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Product> disable(@PathVariable String id) {
        Product updated = productService.setStatus(id, false);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Product> enable(@PathVariable String id) {
        Product updated = productService.setStatus(id, true);
        return ResponseEntity.ok(updated);
    }
}
