package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {
    List<ProductReview> findByProductId(String productId);
}
