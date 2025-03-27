package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReview {

    @Id
    private String id = UUID.randomUUID().toString();

    // Produto avaliado
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Cliente que fez a avaliação
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    // Nota de 1 a 5
    private Integer rating;

    // Comentário opcional
    private String comment;
}
