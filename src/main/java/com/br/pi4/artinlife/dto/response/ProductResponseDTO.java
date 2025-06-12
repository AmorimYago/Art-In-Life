package com.br.pi4.artinlife.dto.response;

import com.br.pi4.artinlife.model.Product;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String mainImage; // Path da imagem principal para o frontend

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        if (product.getImages() != null && !product.getImages().isEmpty()) {

            this.mainImage = product.getImages().get(0).getPath();
        } else {
            this.mainImage = "placeholder.jpg";
        }
    }
}