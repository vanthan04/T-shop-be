package com.paymentservice.repository;

import com.paymentservice.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentModel, UUID> {
    Optional<PaymentModel> findByOrderId(UUID orderId);
}
