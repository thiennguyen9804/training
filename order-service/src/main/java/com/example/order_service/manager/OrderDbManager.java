package com.example.order_service.manager;

import com.example.order_service.model.dto.OrderRequest;
import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.KafkaOutbox;
import com.example.order_service.model.entity.Order;
import com.example.order_service.model.entity.OrderItem;
import com.example.order_service.repository.KafkaOutboxRepository;
import com.example.order_service.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderDbManager {

  private final OrderRepository orderRepository;
  private final ObjectMapper mapper = new ObjectMapper();
  private final KafkaOutboxRepository outboxRepository;

  @Transactional
  public Order saveOrderTx(OrderRequest orderRequest) {
    Order newOrder = new Order();
    newOrder.setCustomerId(orderRequest.customerId());

    for (OrderRequest.ItemDto itemDto : orderRequest.items()) {
      OrderItem dbItem = new OrderItem(itemDto.id(), itemDto.quantity());
      newOrder.addOrderItem(dbItem);
    }
    var createdOrder = orderRepository.save(newOrder);
    var shipmentEvent =
            new ShipmentEvent(createdOrder.getId(), createdOrder.getCustomerId(), "CREATED");
    var outbox = new KafkaOutbox();
    try {
      outbox.setPayload(mapper.writeValueAsString(shipmentEvent));
    } catch (JsonProcessingException ignored) {
    }
    outbox.setTopic("shipping-topic");
    outboxRepository.save(outbox);
    return createdOrder;
  }
}
