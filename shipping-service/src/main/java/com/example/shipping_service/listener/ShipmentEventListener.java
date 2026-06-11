package com.example.shipping_service.listener;

import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.event.UpdateShipmentEvent;
import com.example.shipping_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentEventListener {
  private final ShippingService shippingService;

  @KafkaListener(topics = "shipping-topic", groupId = "order-service-group")
  public void handleShippingTopic(ShipmentEvent event) {
    shippingService.saveShipping(event);
  }

  @KafkaListener(topics = "update-shipping-topic", groupId = "stock-service-group")
  public void handleUpdateShippingTopic(UpdateShipmentEvent event) {
    shippingService.updateShipping(event);
  }
}
