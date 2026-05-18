package com.booknest.cartservice.service;

import com.booknest.cartservice.dto.CartRequest;
import com.booknest.cartservice.model.Cart;
import com.booknest.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public List<Cart> addToCart(CartRequest request) {
        if (request.getUserId() == null || request.getProductId() == null) {
            return Collections.emptyList();
        }

        Cart existing = cartRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId());
        if (existing != null) {
            int incomingQty = Math.max(1, request.getQuantity());
            existing.setQuantity(Math.max(1, existing.getQuantity()) + incomingQty);
            if (request.getProductName() != null && !request.getProductName().isBlank()) {
                existing.setProductName(request.getProductName());
            }
            if (request.getPrice() > 0) {
                existing.setPrice(request.getPrice());
            }
            if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
                existing.setImageUrl(request.getImageUrl());
            }
            cartRepository.save(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(request.getUserId());
            cart.setProductId(request.getProductId());
            cart.setProductName(request.getProductName());
            cart.setPrice(request.getPrice());
            cart.setImageUrl(request.getImageUrl());
            cart.setQuantity(Math.max(1, request.getQuantity()));
            cartRepository.save(cart);
        }

        return cartRepository.findByUserId(request.getUserId());
    }

    public List<Cart> updateCartItem(CartRequest request) {
        if (request.getUserId() == null || request.getProductId() == null) {
            return Collections.emptyList();
        }

        Cart existing = cartRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId());
        if (existing == null) {
            return cartRepository.findByUserId(request.getUserId());
        }

        int nextQuantity = Math.max(1, request.getQuantity());
        existing.setQuantity(nextQuantity);
        cartRepository.save(existing);

        return cartRepository.findByUserId(request.getUserId());
    }

    public List<Cart> getUserCart(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public List<Cart> removeItem(String id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            return Collections.emptyList();
        }
        String userId = cart.getUserId();
        cartRepository.deleteById(id);
        return cartRepository.findByUserId(userId);
    }

    public List<Cart> clearCart(String userId) {
        List<Cart> items = cartRepository.findByUserId(userId);
        cartRepository.deleteAll(items);
        return cartRepository.findByUserId(userId);
    }
}