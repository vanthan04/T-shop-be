package com.cartservice.services;

import com.cartservice.event.consumer.user.UserCreatedEvent;
import com.cartservice.models.CartModel;
import com.cartservice.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class HandleEvent {
    private final CartRepository cartRepository;

    @KafkaListener(topics = "userCreated", groupId = "cartGroup")
    public void handleUserCreated(UserCreatedEvent userCreatedEvent){
        System.out.println(userCreatedEvent);
        CartModel cartModel = new CartModel(
                userCreatedEvent.getUserId(),
                null,
                LocalDateTime.now()
        );
        cartRepository.save(cartModel);
    }
}
