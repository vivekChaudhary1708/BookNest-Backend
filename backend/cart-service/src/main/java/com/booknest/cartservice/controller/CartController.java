package com.booknest.cartservice.controller;

import com.booknest.cartservice.dto.CartRequest;
import com.booknest.cartservice.model.Cart;
import com.booknest.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public List<Cart> addToCart(@RequestBody CartRequest request) {
        return cartService.addToCart(request);
    }

    @PutMapping("/update")
    public List<Cart> updateCartItem(@RequestBody CartRequest request) {
        return cartService.updateCartItem(request);
    }

    @GetMapping("/{userId}")
    public List<Cart> getUserCart(@PathVariable String userId) {
        return cartService.getUserCart(userId);
    }

    @DeleteMapping("/remove/{id}")
    public List<Cart> removeItem(@PathVariable String id) {
        return cartService.removeItem(id);
    }

    @DeleteMapping("/clear/{userId}")
    public List<Cart> clearCart(@PathVariable String userId) {
        return cartService.clearCart(userId);
    }
}