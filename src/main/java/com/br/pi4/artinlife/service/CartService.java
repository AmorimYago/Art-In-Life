package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.model.Cart;
import com.br.pi4.artinlife.model.CartItem;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.model.Product;
import com.br.pi4.artinlife.repository.CartItemRepository;
import com.br.pi4.artinlife.repository.CartRepository;
import com.br.pi4.artinlife.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByClient(Client client) {
        return cartRepository.findByClient(client)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setClient(client);
                    return cartRepository.save(cart);
                });
    }

    public CartItem addItemToCart(Client client, Long productId, int quantity) {
        Cart cart = getCartByClient(client);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(product.getPrice());

        return cartItemRepository.save(cartItem);
    }

    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getCartItems());
    }
}
