package com.orderservice.service;

import com.orderservice.dto.request.OrderItemRequest;
import com.orderservice.model.OrderLineModel;
import com.orderservice.model.OrderModel;
import com.orderservice.repository.OrderLineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderLineService {
    private final OrderLineRepository orderLineRepository;

    public void createOrderLines(OrderModel order, List<OrderItemRequest> requests) {
        List<OrderLineModel> orderLines = requests.stream().map(request -> {
            OrderLineModel orderLine = new OrderLineModel();
            orderLine.setOrderLineId(UUID.randomUUID());
            orderLine.setOrder(order);
            orderLine.setProductId(request.getProductId());
            orderLine.setQuantity(request.getQuantity());
            orderLine.setPrice(request.getPrice());
            return orderLine;
        }).toList();

        orderLineRepository.saveAll(orderLines);
    }


}
