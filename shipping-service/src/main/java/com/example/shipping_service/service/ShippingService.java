package com.example.shipping_service.service;

import com.example.shipping_service.client.CustomerClientService;
import com.example.shipping_service.entity.Shipping;
import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.repository.ShippingRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingService {
  private final ShippingRepository shippingRepository;
  private final CustomerClientService customerClientService;

  public void saveShipping(ShipmentEvent event) {
    var trackingNumber = UUID.randomUUID().toString();
    var address = customerClientService.getCustomerAddress(event.customerId());
    var entity = new Shipping();
    entity.setAddress(address);
    entity.setOrderId(event.orderId());
    entity.setShippingStatus(event.status());
    entity.setTrackingNumber(trackingNumber);
    shippingRepository.save(entity);
  }
}
