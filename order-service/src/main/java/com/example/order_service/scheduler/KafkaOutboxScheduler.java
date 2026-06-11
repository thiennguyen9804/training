package com.example.order_service.scheduler;

import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.KafkaOutbox;
import com.example.order_service.repository.KafkaOutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaOutboxScheduler {
  private final KafkaTemplate<Long, ShipmentEvent> kafkaTemplate;
  private final ObjectMapper mapper = new ObjectMapper();
  private final KafkaOutboxRepository outboxRepository;
  @Lazy @Autowired private KafkaOutboxScheduler self;
  private final Logger logger = LogManager.getLogger(getClass());

  @Scheduled(fixedDelay = 30000)
  public void outboxResend() {
    var outboxList = outboxRepository.findByStatus("PENDING");
    outboxList.forEach(
        outbox -> {
          try {
            self.sendOutbox(outbox);
          } catch (ExecutionException | InterruptedException e) {
            logger.error("Failed to send outbox: {}", outbox.getId(), e);
          }
        });
  }

  @Retryable(
      retryFor = {ExecutionException.class, InterruptedException.class},
      backoff = @Backoff(delay = 1000, multiplier = 1.3))
  public void sendOutbox(KafkaOutbox outbox) throws ExecutionException, InterruptedException {
    ShipmentEvent event = null;
    try {
      event = mapper.readValue(outbox.getPayload(), ShipmentEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    logger.info("Start sending event with outbox ID: {} to broker", outbox.getId());
    kafkaTemplate
        .send("shipping-topic", outbox.getId(), event)
        .whenComplete(
            (res, ex) -> {
              if (ex == null) {
                outboxRepository.deleteById(outbox.getId());
              } else {
                logger.warn(
                    "Message with id: {} in outbox table failed to send with topic: shipping-topic",
                    outbox.getId());
              }
            });
  }
}
