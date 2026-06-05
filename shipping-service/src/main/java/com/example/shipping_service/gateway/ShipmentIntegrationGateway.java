package com.example.shipping_service.gateway;

import com.example.shipping_service.event.ShipmentEvent;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "primaryChannel")
public interface ShipmentIntegrationGateway {
  void sendToPipeline(ShipmentEvent event);
}
