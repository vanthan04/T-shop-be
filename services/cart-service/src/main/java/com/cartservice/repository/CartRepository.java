package com.cartservice.repository;

import com.cartservice.models.CartModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<CartModel, UUID> {
}
