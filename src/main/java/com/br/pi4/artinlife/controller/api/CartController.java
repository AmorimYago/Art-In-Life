package com.br.pi4.artinlife.controller.api;

import com.br.pi4.artinlife.dto.request.CartItemRequest;
import com.br.pi4.artinlife.model.Cart;
import com.br.pi4.artinlife.model.CartItem;
import com.br.pi4.artinlife.model.Client;
import com.br.pi4.artinlife.repository.CartRepository;
import com.br.pi4.artinlife.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;


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

    @PutMapping("/update")
    public ResponseEntity<Cart> updateItem(@AuthenticationPrincipal Client client,
                                           @RequestParam Long productId,
                                           @RequestParam int quantity) {
        Cart cart = cartService.updateItemQuantity(client, productId, quantity);
        return ResponseEntity.ok(cart);
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

    @PostMapping("/shipping")
    public ResponseEntity<Cart> calculateShipping(@AuthenticationPrincipal Client client,
                                                  @RequestParam BigDecimal shippingCost) {
        Cart cart = cartService.calculateShipping(client, shippingCost);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncCart(@AuthenticationPrincipal Client client, @RequestBody List<CartItemRequest> items) {
        for (CartItemRequest item : items) {
            cartService.addItemToCart(client, item.getProductId(), item.getQuantity());
        }

        // Corrige o client_id no cart, se necessário
        Cart cart = cartService.getCartByClient(client);
        if (cart.getClient() == null) {
            cart.setClient(client);
            cartRepository.save(cart);
        }

        return ResponseEntity.ok().build();
    }




}
