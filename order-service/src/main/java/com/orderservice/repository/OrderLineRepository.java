package com.orderservice.repository;

import com.orderservice.model.OrderLineModel;
import com.orderservice.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLineModel, UUID> {
    List<OrderLineModel> findAllByOrder(OrderModel order);
}
