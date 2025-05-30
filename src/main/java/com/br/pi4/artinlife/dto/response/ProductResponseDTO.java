package com.br.pi4.artinlife.dto.response;

import com.br.pi4.artinlife.model.Product; // Importe sua entidade Product
import lombok.Data; // Para getters e setters

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String mainImage; // Path da imagem principal para o frontend

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        // Lógica para pegar a imagem principal. Ajuste conforme sua classe ProductImage.
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            // Supondo que ProductImage tem um método getPath()
            this.mainImage = product.getImages().get(0).getPath();
        } else {
            this.mainImage = "placeholder.jpg"; // Imagem de fallback
        }
    }
}