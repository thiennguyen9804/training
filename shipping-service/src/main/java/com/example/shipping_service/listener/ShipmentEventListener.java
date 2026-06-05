package com.example.shipping_service.listener;

import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.gateway.ShipmentIntegrationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentEventListener {
  private final ShipmentIntegrationGateway gateway;

  @KafkaListener(topics = "shipping-topic", groupId = "order-service-group")
  public void printShippingInfo(@Payload ShipmentEvent event) {
    gateway.sendToPipeline(event);
  }
}
