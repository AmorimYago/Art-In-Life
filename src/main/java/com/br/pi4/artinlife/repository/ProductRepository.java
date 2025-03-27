package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    // Aqui você pode adicionar buscas personalizadas depois, como:
    // List<Product> findByStatusTrue();
}
