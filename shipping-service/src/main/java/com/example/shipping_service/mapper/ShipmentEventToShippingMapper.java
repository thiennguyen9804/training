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

  @Mapping(target = "address", expression = "java(service.getCustomerAddress(event.customerId()))")
  @Mapping(target = "trackingNumber", expression = "java(java.util.UUID.randomUUID().toString())")
  @Mapping(target = "shippingStatus", expression = "java(event.status())")
  public abstract Shipping eventToShipping(ShipmentEvent event);
}
