package com.cartservice.services;

import com.cartservice.exception.AppException;
import com.cartservice.exception.ErrorCode;
import com.cartservice.models.CartItem;
import com.cartservice.models.CartModel;
import com.cartservice.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public CartModel getCartByUserId(UUID userId){
        Optional<CartModel> existed = cartRepository.findById(userId);
        if (existed.isEmpty()){
            CartModel cartModel = new CartModel(
                    userId,
                    null,
                    LocalDateTime.now()
            );
            return cartRepository.save(cartModel);
        }
        return existed.get();
    }

    public CartModel getOrCreateCart(UUID userId) {
        return cartRepository.findById(userId).orElseGet(() -> {
            CartModel newCart = new CartModel(userId);
            return cartRepository.save(newCart);
        });
    }

    public CartItem addProduct(UUID userId, UUID productId, int quantity) {
        CartModel cart = getOrCreateCart(userId);
        cart.addNewItem(productId, quantity);
        cartRepository.save(cart);
        return cart.getItems().stream().filter(i -> i.getProductId().equals(productId)).findFirst().get();
    }

    public CartItem updateProductQuantity(UUID userId, UUID productId, int quantity) {
        CartModel cart = getOrCreateCart(userId);
        cart.updateQuantity(productId, quantity);
        cartRepository.save(cart);
        return cart.getItems().stream().filter(i -> i.getProductId().equals(productId)).findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_PRODUCT_NOT_FOUND));
    }

    public void removeProduct(UUID userId, UUID productId) {
        CartModel cart = getOrCreateCart(userId);
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public List<CartItem> getAllItems(UUID userId) {
        return getOrCreateCart(userId).getItems();
    }


    public void clearItems(UUID userId, List<UUID> productIds) {
        CartModel cart = getOrCreateCart(userId);
        productIds.forEach(cart::removeItem);
        cartRepository.save(cart);
    }
}
