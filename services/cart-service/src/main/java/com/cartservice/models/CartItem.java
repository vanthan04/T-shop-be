package com.cartservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @Column(name = "cartItem_id", nullable = false)
    private UUID cartItemId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private CartModel cart;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    private int quantity;
}
