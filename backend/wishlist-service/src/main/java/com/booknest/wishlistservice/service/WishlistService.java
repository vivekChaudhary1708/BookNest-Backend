package com.booknest.wishlistservice.service;

import com.booknest.wishlistservice.dto.WishlistRequest;
import com.booknest.wishlistservice.model.Wishlist;
import com.booknest.wishlistservice.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public List<Wishlist> addToWishlist(WishlistRequest request) {
        if (request.getUserId() == null || request.getProductId() == null) {
            return Collections.emptyList();
        }
        Wishlist existing = wishlistRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId());
        if (existing == null) {
            Wishlist item = new Wishlist();
            item.setUserId(request.getUserId());
            item.setProductId(request.getProductId());
            item.setProductName(request.getProductName());
            item.setPrice(request.getPrice());
            wishlistRepository.save(item);
        }
        return wishlistRepository.findByUserId(request.getUserId());
    }

    public List<Wishlist> getUserWishlist(String userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public List<Wishlist> removeItem(String id) {
        Wishlist item = wishlistRepository.findById(id).orElse(null);
        if (item == null) {
            return Collections.emptyList();
        }
        String userId = item.getUserId();
        wishlistRepository.deleteById(id);
        return wishlistRepository.findByUserId(userId);
    }

    public List<Wishlist> clearWishlist(String userId) {
        List<Wishlist> items = wishlistRepository.findByUserId(userId);
        wishlistRepository.deleteAll(items);
        return wishlistRepository.findByUserId(userId);
    }
}