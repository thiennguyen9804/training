package com.example.shipping_service.event;

import java.io.Serializable;

public record UpdateShipmentEvent(
        Long id,
        String status
) implements Serializable {
}
