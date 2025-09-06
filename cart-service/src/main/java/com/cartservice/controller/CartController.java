package com.cartservice.controller;

import com.cartservice.dto.request.AddNewProductToCart;
import com.cartservice.dto.response.ApiResponse;
import com.cartservice.dto.response.EnumCode;
import com.cartservice.models.CartItem;
import com.cartservice.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ApiResponse<CartItem> addProductToCart(@RequestBody AddNewProductToCart request) {
        CartItem item = cartService.addProduct(request.getUserId(), request.getProductId(), request.getQuantity());
        return new ApiResponse<>(EnumCode.CART_ITEM_ADDED, item);
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<CartItem>> getAllItemsByUser(@PathVariable UUID userId) {
        List<CartItem> items = cartService.getAllItems(userId);
        return new ApiResponse<>(EnumCode.CART_ITEMS_FETCHED, items);
    }

    @PutMapping("/{userId}/increase/{productId}")
    public ApiResponse<CartItem> increaseQuantity(@PathVariable UUID userId, @PathVariable UUID productId) {
        CartItem item = cartService.addProduct(userId, productId, 1);
        return new ApiResponse<>(EnumCode.CART_ITEM_UPDATED, item);
    }

    @PutMapping("/{userId}/decrease/{productId}")
    public ApiResponse<CartItem> decreaseQuantity(@PathVariable UUID userId, @PathVariable UUID productId) {
        CartItem item = cartService.addProduct(userId, productId, -1); // Giảm 1
        return new ApiResponse<>(EnumCode.CART_ITEM_UPDATED, item);
    }

    @PutMapping("/{userId}/set/{productId}")
    public ApiResponse<CartItem> setQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        CartItem item = cartService.updateProductQuantity(userId, productId, quantity);
        return new ApiResponse<>(EnumCode.CART_ITEM_UPDATED, item);
    }

    @DeleteMapping("/{userId}/product/{productId}")
    public ApiResponse<Void> deleteItem(@PathVariable UUID userId, @PathVariable UUID productId) {
        cartService.removeProduct(userId, productId);
        return new ApiResponse<>(EnumCode.CART_ITEM_REMOVED, null);
    }

    @DeleteMapping("/{userId}/batch")
    public ApiResponse<Void> clearMultipleProducts(
            @PathVariable UUID userId,
            @RequestBody List<UUID> productIds) {
        cartService.clearItems(userId, productIds);
        return new ApiResponse<>(EnumCode.CART_ITEM_REMOVED, null);
    }

    @GetMapping("/{userId}/count")
    public ApiResponse<Integer> getTotalItemCount(@PathVariable UUID userId) {
        int total = cartService.getAllItems(userId).stream().mapToInt(CartItem::getQuantity).sum();
        return new ApiResponse<>(EnumCode.CART_ITEM_COUNTED, total);
    }
}
