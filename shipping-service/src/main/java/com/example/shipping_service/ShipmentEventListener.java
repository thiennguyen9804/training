package com.example.shipping_service;

import com.example.shipping_service.client.CustomerClientService;
import com.example.shipping_service.entity.Shipping;
import com.example.shipping_service.event.ShipmentEvent;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShipmentEventListener {
    private final ShippingRepository shippingRepository;
    private final CustomerClientService customerClientService;

    @KafkaListener(topics = "shipping-topic", groupId = "order-service-group")
    public void printShippingInfo(
        @Payload ShipmentEvent event
    ) {
        var trackingNumber = UUID.randomUUID().toString();
        var address = customerClientService.getCustomerAddress(event.customerId());
        var entity = new Shipping();
        entity.setAddress(address);
        entity.setShippingStatus(event.status());
        entity.setTrackingNumber(trackingNumber);
        shippingRepository.save(entity);
    }
}
