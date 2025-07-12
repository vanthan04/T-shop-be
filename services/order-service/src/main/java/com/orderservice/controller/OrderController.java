package com.orderservice.controller;

import com.orderservice.dto.request.OrderRequest;
import com.orderservice.dto.response.ApiResponse;
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
        return new ApiResponse<>(true, "Lay danh sach don hang thanh cong", orderList);
    }
    @PostMapping("/checkout")
    public ApiResponse<OrderModel> createOrder(@RequestBody OrderRequest request) {
        OrderModel order =  orderService.checkoutOrder(request);
        return new ApiResponse<>(true, "Tạo đơn hàng thành công", order) ;
    }

    //Xem lich su don hang cua nguoi dung
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderModel>> getAllOrdersByUserId(@PathVariable UUID userId){
        List<OrderModel> orderList = orderService.getAllOrdersByUserId(userId);
        return new ApiResponse<>(true, "Lay danh sach don hang cua nguoi dung thanh cong", orderList);
    }

    @GetMapping("/detail/{orderId}")
    public ApiResponse<List<OrderLineModel>> getDetailOrder(@PathVariable UUID orderId){
        List<OrderLineModel> orderLineModels = orderService.getDetailOrder(orderId);
        return new ApiResponse<>(true, "Lay thong tin chi tiet don hang", orderLineModels);
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponse<OrderModel> cancelOrderByOrderId(@PathVariable UUID orderId){
        OrderModel orderModel = orderService.cancelOrderByOrderId(orderId);
        return new ApiResponse<>(true, "Huy don hang thanh cong", orderModel);
    }
}
