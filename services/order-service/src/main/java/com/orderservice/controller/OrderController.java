package com.orderservice.controller;

import com.orderservice.dto.request.OrderRequest;
import com.orderservice.dto.response.ApiResponse;
import com.orderservice.dto.response.EnumCode;
import com.orderservice.model.OrderLineModel;
import com.orderservice.model.OrderModel;
import com.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping()
    public ApiResponse<List<OrderModel>> getAllOrders(){
        List<OrderModel> orderList = orderService.getAllOrders();
        return new ApiResponse<>(EnumCode.GET_ALL_ORDERS_SUCCESS, orderList);
    }
    @PostMapping("")
    public ApiResponse<OrderModel> createOrder(@RequestBody OrderRequest request) {
        OrderModel order = orderService.checkoutOrder(request);
        return new ApiResponse<>(EnumCode.CREATE_ORDER_SUCCESS, order) ;
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderModel>> getAllOrdersByUserId(@PathVariable UUID userId){
        List<OrderModel> orderList = orderService.getAllOrdersByUserId(userId);
        return new ApiResponse<>(EnumCode.GET_ALL_ORDERS_SUCCESS, orderList);
    }

    @GetMapping("/detail/{orderId}")
    public ApiResponse<List<OrderLineModel>> getDetailOrder(@PathVariable UUID orderId){
        List<OrderLineModel> orderLineModels = orderService.getDetailOrder(orderId);
        return new ApiResponse<>(EnumCode.GET_ORDER_DETAIL_SUCCESS, orderLineModels);
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponse<OrderModel> cancelOrderByOrderId(@PathVariable UUID orderId){
        OrderModel orderModel = orderService.cancelOrderByOrderId(orderId);
        return new ApiResponse<>(EnumCode.CANCEL_ORDER_SUCCESS, orderModel);
    }
}
