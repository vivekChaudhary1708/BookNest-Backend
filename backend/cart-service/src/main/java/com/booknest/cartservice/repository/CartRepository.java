package com.booknest.cartservice.repository;

import com.booknest.cartservice.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartRepository extends MongoRepository<Cart, String> {

    List<Cart> findByUserId(String userId);

    Cart findByUserIdAndProductId(String userId, String productId);
}