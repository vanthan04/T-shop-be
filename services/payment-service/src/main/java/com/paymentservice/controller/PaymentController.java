package com.paymentservice.controller;

import com.paymentservice.dto.request.PaymentCreatedRequest;
import com.paymentservice.dto.response.ApiResponse;
import com.paymentservice.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/success")
    public ApiResponse<Void> isSuccess(){
        paymentService.isSuccess();
        return new ApiResponse<>(true, "Thanh cong", null);
    }

    @PostMapping("/failed")
    public boolean isFailed(){
        paymentService.isFailed();
        return false;
    }

    @PostMapping("/checkout")
    public ApiResponse<String> urlCheckout(@RequestBody PaymentCreatedRequest request){
        System.out.println(request);
        return new ApiResponse<>(true, "Chuoi thanh toan", "hehehehehehe");
    }

    @PostMapping("/refund")
    public ApiResponse<String> refund(@RequestBody String text){
        System.out.println(text);
        return new ApiResponse<>(true, "Tra hang", "Tra hang nhe");
    }

}
