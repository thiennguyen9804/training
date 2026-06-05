package com.example.shipping_service.mapper;

import com.example.shipping_service.client.CustomerClientService;
import com.example.shipping_service.entity.Shipping;
import com.example.shipping_service.event.ShipmentEvent;
import java.util.UUID;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ShipmentEventToShippingMapper {
  @Autowired protected CustomerClientService service;

  @BeforeMapping
  protected void preEnrichShipping(ShipmentEvent event, @MappingTarget Shipping shipping) {
    shipping.setOrderId(event.orderId());
    shipping.setShippingStatus(event.status());
    shipping.setAddress(service.getCustomerAddress(event.customerId()));
  }

  @AfterMapping
  void postEnrichShipping(@MappingTarget Shipping shipping) {
    shipping.setTrackingNumber(UUID.randomUUID().toString());
  }

  public abstract Shipping eventToShipping(ShipmentEvent event);
}
