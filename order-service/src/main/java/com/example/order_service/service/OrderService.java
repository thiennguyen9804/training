package com.example.order_service.service;

import com.example.order_service.client.ShippingServiceClient;
import com.example.order_service.client.StockServiceClient;
import com.example.order_service.manager.OrderDbManager;
import com.example.order_service.model.dto.OrderRequest;
import com.example.order_service.model.dto.ShipmentEvent;
import com.example.order_service.model.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final StockServiceClient stockServiceClient;
  private final OrderDbManager orderDbManager;
  private final ShippingServiceClient shippingServiceClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public Order createOrder(OrderRequest orderRequest) {
    checkAllItemsStock(orderRequest);
    var createdOrder = orderDbManager.saveOrderTx(orderRequest);
    var shipmentEvent =
        new ShipmentEvent(createdOrder.getId(), createdOrder.getCustomerId(), "CREATED");
    shippingServiceClient.send(shipmentEvent);
    return createdOrder;
  }

  private void checkAllItemsStock(OrderRequest orderRequest) {
    stockServiceClient.verifyProductStocks(orderRequest.items());
  }
}
