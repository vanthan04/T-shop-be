package com.cartservice.services;

import com.cartservice.dto.request.AddNewProductToCart;
import com.cartservice.exception.AppException;
import com.cartservice.models.CartItem;
import com.cartservice.models.CartModel;
import com.cartservice.repository.CartItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartItemService {
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;

    public CartItem addNewProductToCart(AddNewProductToCart request){
        CartModel cart = cartService.getCartByUserId(request.getUserId());

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProductId(cart, request.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            return cartItemRepository.save(item);
        }

        CartItem newItem = new CartItem(
                UUID.randomUUID(),
                cart,
                request.getProductId(),
                request.getQuantity()
        );
        return cartItemRepository.save(newItem);
    }


    public CartItem increaseQuantityByProductId(UUID userId, UUID productId) {
        return updateQuantity(userId, productId, 1);
    }

    public CartItem decreaseQuantityByProductId(UUID userId, UUID productId) {
        return updateQuantity(userId, productId, -1);
    }

    private CartItem updateQuantity(UUID userId, UUID productId, int delta) {
        CartModel cart = cartService.getCartByUserId(userId);
        Optional<CartItem> cartItemOpt = cartItemRepository.findByCartAndProductId(cart, productId);

        if (cartItemOpt.isEmpty()) {
            throw new AppException(400, "Không tìm thấy sản phẩm trong giỏ hàng!");
        }

        CartItem item = cartItemOpt.get();
        int newQuantity = item.getQuantity() + delta;

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
            throw new AppException(200, "Đã xoá sản phẩm khỏi giỏ hàng vì số lượng = 0");
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    public CartItem setQuantity(UUID userId, UUID productId, int quantity) {
        if (quantity <= 0) {
            deleteCartItemByProductId(userId, productId);
            throw new AppException(200, "Đã xoá sản phẩm vì số lượng = 0");
        }

        CartModel cart = cartService.getCartByUserId(userId);
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm trong giỏ hàng!"));

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }


    public List<CartItem> getAllCartItemsByCartId(UUID userId){
        CartModel cart = cartService.getCartByUserId(userId);

        return cartItemRepository.findByCart(cart);
    }

    public void deleteCartItemByProductId(UUID userId, UUID productId){
        CartModel cart = cartService.getCartByUserId(userId);

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndProductId(cart, productId);
         if (cartItem.isEmpty()){
             throw new AppException(400, "Không tìm thấy sản phẩm trong giỏ hàng!");
         }
         cartItemRepository.delete(cartItem.get());
    }

    public int getTotalItemCount(UUID userId) {
        CartModel cart = cartService.getCartByUserId(userId);
        List<CartItem> items = cartItemRepository.findByCart(cart);
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public void clearCartWithProductIds(UUID userId, List<UUID> productIds) {
        CartModel cart = cartService.getCartByUserId(userId);
        List<CartItem> itemsToDelete = cartItemRepository.findByCartAndProductIdIn(cart, productIds);
        cartItemRepository.deleteAll(itemsToDelete);
    }



}
