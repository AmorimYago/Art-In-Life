package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.model.Cart;
import com.br.pi4.artinlife.model.CartItem;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal Client client) {
        Cart cart = cartService.getCartByClient(client);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addItem(@AuthenticationPrincipal Client client,
                                            @RequestParam Long productId,
                                            @RequestParam int quantity) {
        CartItem cartItem = cartService.addItemToCart(client, productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Client client) {
        Cart cart = cartService.getCartByClient(client);
        cartService.clearCart(cart);
        return ResponseEntity.noContent().build();
    }
}
