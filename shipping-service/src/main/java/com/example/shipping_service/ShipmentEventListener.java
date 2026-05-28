package com.example.shipping_service;

import org.springframework.kafka.annotation.KafkaListener;

@KafkaListener(topics = "shipping-topic")
public class ShipmentEventListener {

}
