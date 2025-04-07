package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.ProductDTO;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.model.ProductImage;
import com.br.pi4.artinlife.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional
    public Product create(ProductDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .status(true)
                .rating(0.0f)
                .build();

        if (dto.getImagePaths() != null && !dto.getImagePaths().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (String path : dto.getImagePaths()) {
                boolean isMain = path.equals(dto.getMainImagePath());
                ProductImage image = new ProductImage(path, isMain, product);
                images.add(image);
            }
            product.setImages(images);
        }

        return repository.save(product);
    }

    @Transactional
    public Product update(String id, ProductDTO dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        // Atualiza imagens, se fornecido
        if (dto.getImagePaths() != null) {
            product.getImages().clear();
            for (String path : dto.getImagePaths()) {
                boolean isMain = path.equals(dto.getMainImagePath());
                ProductImage image = new ProductImage(path, isMain, product);
                product.getImages().add(image);
            }
        }

        return repository.save(product);
    }

    public Product setStatus(String id, boolean status) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        product.setStatus(status);
        return repository.save(product);
    }

    public Optional<Product> findById(String id) {
        return repository.findById(id);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }
}
