package com.booknest.wishlistservice.repository;

import com.booknest.wishlistservice.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {

    List<Wishlist> findByUserId(String userId);

    Wishlist findByUserIdAndProductId(String userId, String productId);
}