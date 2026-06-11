package com.example.order_service.scheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.KafkaOutbox;
import com.example.order_service.repository.KafkaOutboxRepository;
import com.example.order_service.repository.OrderRepository;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@EnableRetry
@ImportAutoConfiguration(
    exclude = {
      DataSourceAutoConfiguration.class,
    })
@TestPropertySource(
    properties = {
      "STOCK_SERVICE_URI=http://localhost:8080",
      "SECRET_KEY=ABC",
    })
public class KafkaOutboxSchedulerRetryIT {
  @Autowired private KafkaOutboxScheduler scheduler;

  @MockitoBean private KafkaTemplate<Long, ShipmentEvent> kafkaTemplate;

  @MockitoBean private RestTemplate restTemplate;

  @MockitoBean private KafkaOutboxRepository outboxRepository;
  @MockitoBean private OrderRepository orderRepository;

//  @Test
  void whenKafkaFails_thenRetry3Times() throws Exception {
    CompletableFuture<SendResult<Long, ShipmentEvent>> future = new CompletableFuture<>();
    future.completeExceptionally(new RuntimeException("Kafka down"));
    when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

    KafkaOutbox outbox = new KafkaOutbox();
    outbox.setPayload(
        """
                {"orderId":1,"customerId":1,"status":"CREATED"}
                """);
    outbox.setId(1L);

    assertThatThrownBy(() -> scheduler.sendOutbox(outbox)).isInstanceOf(ExecutionException.class);

    verify(kafkaTemplate, times(3)).send(any(), any(), any());
  }
}
