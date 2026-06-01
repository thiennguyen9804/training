package com.example.order_service.service;

import com.example.order_service.client.StockServiceClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.ShipmentEvent;
import com.example.order_service.entity.Order;
import com.example.order_service.manager.OrderDbManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final StockServiceClient stockServiceClient;
  private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;
  private final OrderDbManager orderDbManager;

  public Order createOrder(OrderRequest orderRequest) {
    checkAllItemsStock(orderRequest);
    var createdOrder = orderDbManager.saveOrderTx(orderRequest);
    var shipmentEvent =
        new ShipmentEvent(createdOrder.getId(), createdOrder.getCustomerId(), "CREATED");
    kafkaTemplate.send("shipping-topic", shipmentEvent);
    return createdOrder;
  }

  private void checkAllItemsStock(OrderRequest orderRequest) {
    stockServiceClient.verifyProductStocks(orderRequest.items());
  }
}
