package com.example.shipping_service.service;

import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.mapper.ShipmentEventToShippingMapper;
import com.example.shipping_service.repository.ShippingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShippingService {
  private final ShipmentEventToShippingMapper mapper;
  private final ShippingRepository shippingRepository;

  public void saveShipping(ShipmentEvent event) {
    var entity = mapper.eventToShipping(event);
    shippingRepository.save(entity);
  }
}
