package com.cartservice.models;

import com.cartservice.exception.AppException;
import com.cartservice.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartModel {

    @Id
    private UUID userID;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();

    private LocalDateTime createdAt;

    public CartModel(UUID userId) {
        this.userID = userId;
    }

    public void addNewItem(UUID productId, int quantity){
        if (quantity <=0){
            throw new AppException(ErrorCode.CART_INVALID_ITEM_QUANTITY);
        }
        for(CartItem item : items){
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity()+quantity);
                return;
            }
        }
        items.add(new CartItem(UUID.randomUUID(), productId, quantity));
    }

    public void removeItem(UUID productId){
        items.removeIf(item -> item.getProductId().equals(productId));
    }
    public void updateQuantity(UUID productId, int quantity){
        for (CartItem item : items){
            if (item.getProductId().equals(productId)) {
                if (quantity <=0){
                    removeItem(productId);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
        throw new AppException(ErrorCode.CART_PRODUCT_NOT_FOUND);
    }
}
