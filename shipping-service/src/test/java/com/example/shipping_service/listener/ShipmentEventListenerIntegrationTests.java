package com.example.shipping_service.listener;

import com.example.shipping_service.event.ShipmentEvent;
import com.example.shipping_service.gateway.ShipmentIntegrationGateway;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = { "shipping-topic" }
)
@Profile("kafka-test")
public class ShipmentEventListenerIntegrationTests {
    @Autowired
    private KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
    @MockitoBean
    private ShipmentIntegrationGateway gateway;

//    @Test
    void shouldReceiveShipmentEventAndSendToPipeline() throws Exception {
        ShipmentEvent mockEvent = new ShipmentEvent(1L, 1L, "CREATED");
        kafkaTemplate.send("shipping-topic", mockEvent).get();
        ArgumentCaptor<ShipmentEvent> eventCaptor = ArgumentCaptor.forClass(ShipmentEvent.class);
        verify(gateway, timeout(5000).times(1)).sendToPipeline(eventCaptor.capture());
        ShipmentEvent capturedEvent = eventCaptor.getValue();
        assertEquals(1L, capturedEvent.orderId());
        assertEquals("CREATED", capturedEvent.status());

    }
}
