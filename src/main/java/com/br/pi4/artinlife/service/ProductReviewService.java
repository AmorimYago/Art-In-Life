package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.ProductReviewDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.model.ProductReview;
import com.br.pi4.artinlife.repository.AppUserRepository;
import com.br.pi4.artinlife.repository.ProductRepository;
import com.br.pi4.artinlife.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository userRepository;

    /**
     * Salva uma avaliação de produto e atualiza a média de avaliação.
     */
    public ProductReview createReview(ProductReviewDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        AppUser user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ProductReview review = ProductReview.builder()
                .product(product)
                .user(user)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .build();

        reviewRepository.save(review);

        // Atualiza a média do produto
        List<ProductReview> reviews = reviewRepository.findByProductId(product.getId());
        float avg = (float) reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0.0);
        product.setRating(avg);
        productRepository.save(product);

        return review;
    }
}
