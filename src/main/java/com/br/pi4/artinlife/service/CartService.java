package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.CartItemDTO;
import com.br.pi4.artinlife.model.*;
import com.br.pi4.artinlife.repository.CartItemRepository;
import com.br.pi4.artinlife.repository.CartRepository;
import com.br.pi4.artinlife.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Adiciona um item ao carrinho.
     */
    public Cart addItem(String cartId, CartItemDTO dto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal unitPrice = product.getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));

        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(dto.getQuantity())
                .unitPrice(unitPrice)
                .build();

        cartItemRepository.save(item);

        // Atualiza o total do carrinho
        cart.setTotalPrice(cart.getTotalPrice().add(subtotal));
        return cartRepository.save(cart);
    }
}
