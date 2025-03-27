package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    // Pode adicionar filtros como buscar por produtoId, se quiser no futuro
}
