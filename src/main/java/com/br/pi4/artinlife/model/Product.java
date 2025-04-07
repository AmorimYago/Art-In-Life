package com.br.pi4.artinlife.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /*
    @Id
    private String id = UUID.randomUUID().toString();
     */

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Boolean status = true; // true = ativo, false = desativado

    private Float rating = 0.0f; // média das avaliações (1 a 5)

    // Relacionamento: 1 produto pode ter várias imagens
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;
}
