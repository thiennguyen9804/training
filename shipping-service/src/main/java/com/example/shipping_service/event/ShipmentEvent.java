package com.example.shipping_service.event;

public record ShipmentEvent(Long orderId, Long customerId, String status) {}
