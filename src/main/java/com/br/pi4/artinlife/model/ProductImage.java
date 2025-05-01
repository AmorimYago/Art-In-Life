package com.br.pi4.artinlife.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private boolean isPrimary;

    private String path; // caminho da imagem salva no servidor ou diretório local


    @ManyToOne
    @JoinColumn(name = "product_id") // chave estrangeira para a tabela product
    @JsonBackReference
    private Product product;

    public ProductImage(String path, boolean isPrimary, Product product) {
        this.path = path;
        this.isPrimary = isPrimary;
        this.product = product;
    }
}
