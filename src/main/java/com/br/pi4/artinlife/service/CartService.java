package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.exception.ResourceNotFoundException;
import com.br.pi4.artinlife.model.Cart;
import com.br.pi4.artinlife.model.CartItem;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.repository.CartItemRepository;
import com.br.pi4.artinlife.repository.CartRepository;
import com.br.pi4.artinlife.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getCartByClient(Client client) {
        return cartRepository.findByClient(client)
                .orElseGet(() -> createCart(client));
    }

    public Cart createCart(Client client) {
        Cart cart = Cart.builder()
                .client(client)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .total(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    public CartItem addItemToCart(Client client, Long productId, Integer quantity) {
        Cart cart = getCartByClient(client);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem item;
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            item = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .build();
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        recalculateCartTotal(cart);
        cartRepository.save(cart);
        return item;
    }

    public Cart updateItemQuantity(Client client, Long productId, Integer quantity) {
        Cart cart = getCartByClient(client);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho"));

        item.setQuantity(quantity);
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

        cart.setUpdatedAt(LocalDateTime.now());
        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    public void removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho"));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cart.setUpdatedAt(LocalDateTime.now());
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotal(total.add(cart.getShippingCost()));
    }

    public Cart calculateShipping(Client client, BigDecimal shippingCost) {
        Cart cart = getCartByClient(client);
        cart.setShippingCost(shippingCost);
        recalculateCartTotal(cart);
        return cartRepository.save(cart);
    }

    public void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    public List<CartItem> getItemsByCart(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

}
