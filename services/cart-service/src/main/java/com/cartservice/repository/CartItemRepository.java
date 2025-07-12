package com.cartservice.repository;

import com.cartservice.models.CartItem;
import com.cartservice.models.CartModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByCart(CartModel cart);
    Optional<CartItem> findByCartAndProductId(CartModel Cart, UUID productId);
    List<CartItem> findByCartAndProductIdIn(CartModel cart, List<UUID> productIds);

}
