package com.orderservice.service;

import com.orderservice.dto.request.OrderItemRequest;
import com.orderservice.dto.request.OrderRequest;
import com.orderservice.dto.response.ApiResponse;
import com.orderservice.exception.AppException;
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
            throw new AppException(400, "Khong tim thay hoa don!");
        }
        return orderLineRepository.findAllByOrder(orderModel.get());
    }

    @Transactional
    public OrderModel creatOrder(OrderRequest orderRequest){
        OrderModel order = new OrderModel();
        order.setOrderId(UUID.randomUUID());
        order.setUserId(orderRequest.getUserInfo().getUserID());
        order.setEmail(orderRequest.getUserInfo().getEmail());
        order.setAddressShip(orderRequest.getUserInfo().getAddressShip());
        order.setPhone(orderRequest.getUserInfo().getPhone());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderLineModel> orderLines = orderRequest.getItems().stream().map(item -> {
            OrderLineModel line = new OrderLineModel();
            line.setOrderLineId(UUID.randomUUID());
            line.setProductId(item.getProductId());
            line.setQuantity(item.getQuantity());
            line.setOrder(order);
            return line;
        }).toList();

        order.setOrderLines(orderLines);

        return orderRepository.save(order);
    }

    @Transactional
    public OrderModel checkoutOrder(OrderRequest request) {
        //Tao orderId ban dau, truyen vao inventory, neu thanh cong thi luu lich su
        UUID orderId = UUID.randomUUID();

        // Gọi inventory service để kiểm tra và giữ hàng
        boolean inventoryReserved = inventoryClient.reserve(new InventoryRequest(orderId, request.getItems())).isSuccess();
        if (!inventoryReserved) {
            throw new AppException(400, "Không đủ hàng trong kho");
        }

        // Neu thanh cong thi tao don hang
        OrderModel orderModel = creatOrder(request);

        //Goi payment service de tra ve url thanh toan
        ApiResponse<String> payres = paymentClient.checkout(new PaymentCreatedRequest(request.getUserInfo().getUserID(), orderId, new BigDecimal(100000)));

        System.out.println(payres);
        return orderModel;
    }


    @Transactional
    public OrderModel cancelOrderByOrderId(UUID orderId) {
        // Lấy đơn hàng
        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(404, "Không tìm thấy đơn hàng"));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new AppException(400, "Chỉ đơn hàng ở trạng thái PENDING mới được hủy");
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
            throw new AppException(500, "Không thể hoàn hàng về kho");
        }

        // Cập nhật trạng thái đơn
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
