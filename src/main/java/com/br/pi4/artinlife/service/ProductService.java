package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.ProductDTO;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.model.ProductImage;
import com.br.pi4.artinlife.model.ProductReview;
import com.br.pi4.artinlife.repository.ProductRepository;
import com.br.pi4.artinlife.repository.ProductReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductReviewRepository productReviewRepository;

    public List<ProductReview> getReviewsByProductId(Long productId) {
        return productReviewRepository.findByProductId(productId);
    }

    public List<Product> getAllProductsSortedByNewest() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Product create(ProductDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .status(true)
                .rating(dto.getRating())
                .build();

        if (dto.getImagePaths() != null && !dto.getImagePaths().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (String path : dto.getImagePaths()) {
                boolean isMain = path.equals(dto.getMainImagePath());

                ProductImage image = ProductImage.builder()
                        .url("/images/" + path)  // URL pública
                        .path(path)              // caminho físico na pasta
                        .isPrimary(isMain)
                        .product(product)
                        .build();

                images.add(image);
            }
            product.setImages(images);
        }

        return repository.save(product);
    }

    @Transactional
    public void addProductImage(Long productId, String imagePath, boolean isPrimary) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ProductImage image = ProductImage.builder()
                .url("/images/" + imagePath) // ou apenas imagePath se não usar prefixo
                .path(imagePath)
                .isPrimary(isPrimary)
                .product(product)
                .build();

        // Adiciona a imagem na lista e salva
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }
        product.getImages().add(image);
        repository.save(product);
    }


    @Transactional
    public Product update(Long id, ProductDTO dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setRating(dto.getRating());
        // Mantenha outros campos como rating, createdAt, etc. se forem atualizáveis ou precisar deles

        if (dto.getImagePaths() != null) {
            // Limpar imagens existentes ANTES de adicionar novas.
            // A anotação cascade = CascadeType.ALL e orphanRemoval = true em Product.images
            // deve lidar com a remoção de ProductImage órfãs ao limpar a coleção.
            if (product.getImages() != null) { // Adicione um null check
                product.getImages().clear();
            } else {
                product.setImages(new ArrayList<>()); // Inicializa se for nulo
            }


            for (String path : dto.getImagePaths()) {
                boolean isMain = path.equals(dto.getMainImagePath());
                ProductImage image = ProductImage.builder()
                        .url("/images/" + path) // <--- CORREÇÃO AQUI: Construir a URL completa
                        .path(path) // <--- Certifique-se de que o campo 'path' também é setado
                        .isPrimary(isMain)
                        .product(product)
                        .build();
                product.getImages().add(image);
            }
        }

        return repository.save(product);
    }

    public Product setStatus(Long id, boolean status) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        product.setStatus(status);
        return repository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return repository.findById(id);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public List<Product> getActiveProducts() {
        return repository.findByStatusTrue();
    }

    public List<Product> findByCategoryId(Long id) {
        return repository.findByStatusTrue().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Product> searchActiveProductsByName(String name) {
        return repository.findByStatusTrue().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


}
