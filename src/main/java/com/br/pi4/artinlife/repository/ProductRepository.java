package com.br.pi4.artinlife.repository;

import com.br.pi4.artinlife.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Correção aqui: status, não active
    List<Product> findByStatusTrue();

    // E se quiser também ordenar os mais novos primeiro:
    List<Product> findAllByOrderByCreatedAtDesc();
}
