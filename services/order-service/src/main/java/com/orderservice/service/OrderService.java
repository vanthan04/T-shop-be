package com.orderservice.service;

import com.orderservice.dto.request.OrderItemRequest;
import com.orderservice.dto.request.OrderRequest;
import com.orderservice.dto.response.ApiResponse;
import com.orderservice.exception.AppException;
import com.orderservice.exception.ErrorCode;
import com.orderservice.feign.inventory.InventoryClient;
import com.orderservice.feign.inventory.InventoryRequest;
import com.orderservice.feign.payment.PaymentClient;
import com.orderservice.feign.payment.PaymentCreatedRequest;
import com.orderservice.model.OrderLineModel;
import com.orderservice.model.OrderModel;
import com.orderservice.model.OrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderLineRepository orderLineRepository;
    private final PaymentClient paymentClient;

    public List<OrderModel> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<OrderModel> getAllOrdersByUserId(UUID userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public List<OrderLineModel> getDetailOrder(UUID orderId) {
        Optional<OrderModel> orderModel = orderRepository.findById(orderId);
        if (orderModel.isEmpty()){
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        return orderLineRepository.findAllByOrder(orderModel.get());
    }

    @Transactional
    public OrderModel createOrder(OrderRequest orderRequest){
        OrderModel order = new OrderModel();

        order.createOrder(
                UUID.randomUUID(),
                orderRequest.getUserInfo().getUserID(),
                orderRequest.getUserInfo().getEmail(),
                orderRequest.getUserInfo().getAddressShip(),
                orderRequest.getUserInfo().getPhone(),
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now(),
                orderRequest.getItems()
        );

        return orderRepository.save(order);
    }

    @Transactional
    public OrderModel checkoutOrder(OrderRequest request) {
        //Tao orderId ban dau, truyen vao inventory, neu thanh cong thi luu lich su
        UUID orderId = UUID.randomUUID();

        // Gọi inventory service để kiểm tra và giữ hàng
        boolean inventoryReserved = inventoryClient.reserve(new InventoryRequest(orderId, request.getItems())).isSuccess();
        if (!inventoryReserved) {
            throw new AppException(ErrorCode.ORDER_OUT_OF_STOCK);
        }

        // Neu thanh cong thi tao don hang
        OrderModel orderModel = createOrder(request);

        //Goi payment service de tra ve url thanh toan
        ApiResponse<String> payres = paymentClient.checkout(new PaymentCreatedRequest(request.getUserInfo().getUserID(), orderId, new BigDecimal(100000), null));

        System.out.println(payres);
        return orderModel;
    }


    @Transactional
    public OrderModel cancelOrderByOrderId(UUID orderId) {
        // Lấy đơn hàng
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new AppException(ErrorCode.ORDER_CANNOT_CANCEL_IF_NOT_PENDING);
        }

        // Lấy danh sách sản phẩm để hoàn về kho
        List<OrderLineModel> orderItems = orderLineRepository.findAllByOrder(order);

        List<OrderItemRequest> itemRequests = orderItems.stream().map(item -> {
            OrderItemRequest req = new OrderItemRequest();
            req.setProductId(item.getProductId());
            req.setQuantity(item.getQuantity());
            req.setPrice(item.getPrice());
            return req;
        }).toList();


        boolean inventoryReturned = inventoryClient.cancelReserveInventory(orderId).isSuccess();
        if (!inventoryReturned) {
            throw new AppException(ErrorCode.ORDER_CANNOT_RETURN_TO_INVENTORY);
        }

        order.updateOrder(OrderStatus.CANCELLED, LocalDateTime.now());
        return orderRepository.save(order);
    }
}
