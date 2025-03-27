package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.ProductDTO;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.model.ProductImage;
import com.br.pi4.artinlife.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service // Marca essa classe como componente de serviço (lógica de negócio)
@RequiredArgsConstructor // Lombok: cria o construtor automaticamente com os campos finais
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Registra um novo produto com base nos dados recebidos do ProductDTO.
     * Cria as imagens associadas com base nos caminhos fornecidos no DTO.
     *
     * @param dto objeto com os dados do produto (nome, descrição, imagens, etc.)
     * @return o produto salvo no banco de dados
     */
    public Product register(ProductDTO dto) {
        // Cria uma nova lista de imagens para o produto
        List<ProductImage> images = new ArrayList<>();

        if (dto.getImagePaths() != null && !dto.getImagePaths().isEmpty()) {
            for (String path : dto.getImagePaths()) {
                boolean isMain = path.equals(dto.getMainImagePath());

                // Cria uma imagem do produto com o caminho e se é principal
                ProductImage image = ProductImage.builder()
                        .path(path)
                        .mainImage(isMain)
                        .build();

                images.add(image);
            }
        }

        // Cria o objeto Product com os dados recebidos
        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .status(true)
                .rating(0.0f)
                .images(images)
                .build();

        // Associa o produto a cada imagem (relacionamento reverso)
        images.forEach(img -> img.setProduct(product));

        // Salva o produto no banco (as imagens também são salvas por cascade)
        return productRepository.save(product);
    }
}
