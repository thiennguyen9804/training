package com.example.order_service.client;

import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.KafkaOutbox;
import com.example.order_service.repository.KafkaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShippingServiceClient {
  private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
  private final ObjectMapper mapper = new ObjectMapper();
  private final KafkaOutboxRepository outboxRepository;

  @Async
  public void send(ShipmentEvent event) {
    var outbox = new KafkaOutbox();
    try {
      outbox.setPayload(mapper.writeValueAsString(event));
    } catch (JsonProcessingException ignored) {
    }
    outbox.setTopic("shipping-topic");
    outbox.setStatus("PENDING");
    outboxRepository.save(outbox);
  }
}
