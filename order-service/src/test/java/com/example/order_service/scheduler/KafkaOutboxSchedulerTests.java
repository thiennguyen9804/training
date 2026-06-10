package com.example.order_service.scheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.KafkaOutbox;
import com.example.order_service.repository.KafkaOutboxRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KafkaOutboxSchedulerTests {

  @Mock private KafkaTemplate<Long, ShipmentEvent> kafkaTemplate;

  @Mock private KafkaOutboxRepository outboxRepository;

  @InjectMocks private KafkaOutboxScheduler scheduler;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(scheduler, "self", scheduler);
  }

  @Test
  void whenNoPendingRecords_thenSendNeverCalled() {
    when(outboxRepository.findByStatus("PENDING")).thenReturn(List.of());

    scheduler.outboxResend();

    verify(kafkaTemplate, never()).send(any(), any(), any());
  }

  @Test
  void whenPendingRecordExists_thenSendCalledAndStatusSuccess() throws Exception {
    KafkaOutbox outbox = new KafkaOutbox();
    outbox.setPayload(
        """
                {"orderId":1,"customerId":1,"status":"CREATED"}
                """);
    outbox.setId(1L);
    when(outboxRepository.findByStatus("PENDING")).thenReturn(List.of(outbox));

    CompletableFuture<SendResult<Long, ShipmentEvent>> future = new CompletableFuture<>();
    future.complete(mock(SendResult.class));
    when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);
    scheduler.outboxResend();
    assertThat(outbox.getStatus()).isEqualTo("SUCCESS");
    verify(outboxRepository).save(outbox);
  }
}
