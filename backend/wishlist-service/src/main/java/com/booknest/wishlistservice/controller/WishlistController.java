package com.booknest.wishlistservice.controller;

import com.booknest.wishlistservice.dto.WishlistRequest;
import com.booknest.wishlistservice.model.Wishlist;
import com.booknest.wishlistservice.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/add")
    public List<Wishlist> addToWishlist(@RequestBody WishlistRequest request) {
        return wishlistService.addToWishlist(request);
    }

    @GetMapping("/{userId}")
    public List<Wishlist> getUserWishlist(@PathVariable String userId) {
        return wishlistService.getUserWishlist(userId);
    }

    @DeleteMapping("/remove/{id}")
    public List<Wishlist> removeItem(@PathVariable String id) {
        return wishlistService.removeItem(id);
    }

    @DeleteMapping("/clear/{userId}")
    public List<Wishlist> clearWishlist(@PathVariable String userId) {
        return wishlistService.clearWishlist(userId);
    }
}