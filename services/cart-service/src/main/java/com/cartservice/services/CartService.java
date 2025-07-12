package com.cartservice.services;

import com.cartservice.models.CartModel;
import com.cartservice.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public CartModel getCartByUserId(UUID userId){
        Optional<CartModel> existed = cartRepository.findById(userId);
        //Neu khong tim thay gio hang voi userId thi tao moi
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
}
