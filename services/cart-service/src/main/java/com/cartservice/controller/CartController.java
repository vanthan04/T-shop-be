package com.cartservice.controller;

import com.cartservice.dto.request.AddNewProductToCart;
import com.cartservice.dto.response.ApiResponse;
import com.cartservice.models.CartItem;
import com.cartservice.services.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    @PostMapping
    public ApiResponse<CartItem> addProductToCart(@RequestBody AddNewProductToCart request) {
        CartItem item = cartItemService.addNewProductToCart(request);
        return new ApiResponse<>(true, "Thêm sản phẩm vào giỏ hàng thành công", item);
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<CartItem>> getAllItemsByUser(@PathVariable UUID userId) {
        List<CartItem> items = cartItemService.getAllCartItemsByCartId(userId);
        return new ApiResponse<>(true, "Lấy danh sách sản phẩm thành công", items);
    }

    @PutMapping("/{userId}/increase/{productId}")
    public ApiResponse<CartItem> increaseQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        CartItem item = cartItemService.increaseQuantityByProductId(userId, productId);
        return new ApiResponse<>(true, "Tăng số lượng thành công", item);
    }

    @PutMapping("/{userId}/decrease/{productId}")
    public ApiResponse<CartItem> decreaseQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        CartItem item = cartItemService.decreaseQuantityByProductId(userId, productId);
        return new ApiResponse<>(true, "Giảm số lượng thành công", item);
    }

    @PutMapping("/{userId}/set/{productId}")
    public ApiResponse<CartItem> setQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @RequestParam int quantity) {
        CartItem item = cartItemService.setQuantity(userId, productId, quantity);
        return new ApiResponse<>(true, "Cập nhật số lượng thành công", item);
    }

    @DeleteMapping("/{userId}/product/{productId}")
    public ApiResponse<Void> deleteItem(
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        cartItemService.deleteCartItemByProductId(userId, productId);
        return new ApiResponse<>(true, "Xoá sản phẩm khỏi giỏ hàng thành công", null);
    }

    @DeleteMapping("/{userId}/batch")
    public ApiResponse<Void> clearMultipleProducts(
            @PathVariable UUID userId,
            @RequestBody List<UUID> productIds) {
        cartItemService.clearCartWithProductIds(userId, productIds);
        return new ApiResponse<>(true, "Đã xoá các sản phẩm khỏi giỏ hàng", null);
    }

    @GetMapping("/{userId}/count")
    public ApiResponse<Integer> getTotalItemCount(@PathVariable UUID userId) {
        int total = cartItemService.getTotalItemCount(userId);
        return new ApiResponse<>(true, "Tổng số sản phẩm trong giỏ", total);
    }
}
