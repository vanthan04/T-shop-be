package com.orderservice.feign.payment;

import com.orderservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service", path = "/api/v1/payment")
public interface PaymentClient {
    @PostMapping("/checkout")
    ApiResponse<String> checkout(PaymentCreatedRequest request);

    @PostMapping("/refund")
    ApiResponse<String> refund(String request);
}
