package com.orderservice.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private UserInfoRequest userInfo;
    private List<OrderItemRequest> items;

}
