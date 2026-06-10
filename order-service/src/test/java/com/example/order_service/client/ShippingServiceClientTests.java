package com.example.order_service.client;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.KafkaOutbox;
import com.example.order_service.repository.KafkaOutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class ShippingServiceClientTests {

  @Mock private KafkaTemplate<String, ShipmentEvent> kafkaTemplate;

  @Mock private KafkaOutboxRepository outboxRepository;

  @InjectMocks private ShippingServiceClient shippingServiceClient;

  @Test
  void whenSend_thenSaveOutboxWithPendingStatus() {
    ShipmentEvent event = new ShipmentEvent(1L, 1L, "CREATED");
    shippingServiceClient.send(event);

    ArgumentCaptor<KafkaOutbox> captor = ArgumentCaptor.forClass(KafkaOutbox.class);
    verify(outboxRepository).save(captor.capture());

    KafkaOutbox saved = captor.getValue();
    assertThat(saved.getStatus()).isEqualTo("PENDING");
    assertThat(saved.getTopic()).isEqualTo("shipping-topic");
    assertThat(saved.getPayload()).contains("orderId");
    assertThat(saved.getPayload()).contains("status");
  }
}
