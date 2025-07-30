package com.paymentservice.controller;

import com.paymentservice.dto.request.PaymentCreatedRequest;
import com.paymentservice.dto.response.ApiResponse;
import com.paymentservice.dto.response.EnumCode;
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
    public ApiResponse<Void> isSuccess(@RequestBody PaymentCreatedRequest request){
        paymentService.isSuccess(request);
        return new ApiResponse<>(EnumCode.PAYMENT_SUCCESS, null);
    }

    @PostMapping("/failed")
    public ApiResponse<Void> isFailed(@RequestBody PaymentCreatedRequest request){
        paymentService.isFailed(request);
        return new ApiResponse<>(EnumCode.PAYMENT_FAILED, null);
    }

    @PostMapping("/checkout")
    public ApiResponse<String> urlCheckout(@RequestBody PaymentCreatedRequest request){
        String urlPayment = paymentService.createUrlToPayment(request);
        return new ApiResponse<>(EnumCode.PAYMENT_URL, urlPayment);
    }

    @PostMapping("/refund")
    public ApiResponse<String> refund(@RequestBody String text){
        System.out.println(text);
        return new ApiResponse<>(EnumCode.PAYMENT_REFUND, "Tra hang nhe");
    }

}
