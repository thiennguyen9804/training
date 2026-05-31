package com.example.shipping_service.event;

import java.io.Serializable;

public record ShipmentEvent(
        Long orderId,
        Long customerId,
        String status
) implements Serializable {}
