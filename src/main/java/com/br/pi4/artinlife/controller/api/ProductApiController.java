package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.ProductDTO;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.model.ProductReview;
import com.br.pi4.artinlife.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/active")
    public List<Product> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid ProductDTO dto) {
        Product created = productService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody @Valid ProductDTO dto) {
        Product updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Product> disable(@PathVariable Long id) {
        Product updated = productService.setStatus(id, false);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Product> enable(@PathVariable Long id) {
        Product updated = productService.setStatus(id, true);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ProductReview>> getProductReviews(@PathVariable Long productId) {
        List<ProductReview> reviews = productService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/products/newest")
    public ResponseEntity<List<Product>> getNewestProducts() {
        List<Product> products = productService.getAllProductsSortedByNewest();
        return ResponseEntity.ok(products);
    }
}
