package com.cartservice.event.consumer.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UserCreatedEvent {
    private UUID userId;

    public UUID getUserId() {
        return userId;
    }
}
