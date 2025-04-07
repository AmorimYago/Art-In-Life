package com.br.pi4.artinlife.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String id = UUID.randomUUID().toString();
    /*
    @Id
    private String id = UUID.randomUUID().toString();
     */


    private String path; // caminho da imagem salva no servidor ou diretório local

    private boolean mainImage; // true se for a imagem principal do produto

    @ManyToOne
    @JoinColumn(name = "product_id") // chave estrangeira para a tabela product
    @JsonIgnore
    private Product product;

    public ProductImage(String path, boolean mainImage, Product product) {
        this.id = UUID.randomUUID().toString();
        this.path = path;
        this.mainImage = mainImage;
        this.product = product;
    }
}
