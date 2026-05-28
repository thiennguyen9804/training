package com.example.order_service.dto;

import java.io.Serializable;

public record ShipmentEvent(
        Long orderId,
        Long customerId,
        String status
) implements Serializable {}
