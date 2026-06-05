package com.example.order_service.client;

import com.example.order_service.dto.ShipmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingServiceClient {
    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
    @Async
    public void send(ShipmentEvent event) {
        kafkaTemplate.send("shipping-topic", event);
    }
}
