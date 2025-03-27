package com.br.pi4.artinlife.model;

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

    private String path; // caminho da imagem salva no servidor ou diretório local

    private boolean mainImage; // true se for a imagem principal do produto

    @ManyToOne
    @JoinColumn(name = "product_id") // chave estrangeira para a tabela product
    private Product product;
}
