package com.example.shipping_service.listener;

import static org.mockito.Mockito.verify;

import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.gateway.ShipmentIntegrationGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShipmentEventListenerTests {

  @Mock private ShipmentIntegrationGateway gateway;

  @InjectMocks private ShipmentEventListener listener;

  @Test
  void testPrintShippingInfo() {
    ShipmentEvent event = new ShipmentEvent(1L, 1L, "");
    listener.printShippingInfo(event);
    verify(gateway).sendToPipeline(event);
  }
}
