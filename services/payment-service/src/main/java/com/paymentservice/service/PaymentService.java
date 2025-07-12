package com.paymentservice.service;

import com.paymentservice.dto.request.PaymentCreatedRequest;
import com.paymentservice.event.producer.PaymentStatusEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {
    private final KafkaTemplate<String, PaymentStatusEvent> kafkaTemplate;

    public String createUrlToPayment(PaymentCreatedRequest request){
        return "Chuoi de thanh toan";
    }

    public void isSuccess() {
        kafkaTemplate.send("payment-success", new PaymentStatusEvent(true));
    }

    public void isFailed() {
        kafkaTemplate.send("payment-failed", new PaymentStatusEvent(false));
    }
}
