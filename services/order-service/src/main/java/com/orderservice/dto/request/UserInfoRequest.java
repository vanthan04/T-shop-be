package com.orderservice.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserInfoRequest {
    private UUID userID;
    private String email;
    private String addressShip;
    private String phone;

}
