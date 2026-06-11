package com.example.order_service.service;

import com.example.order_service.client.StockServiceClient;
import com.example.order_service.manager.OrderDbManager;
import com.example.order_service.model.dto.OrderRequest;
import com.example.order_service.model.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final StockServiceClient stockServiceClient;
  private final OrderDbManager orderDbManager;

  public Order createOrder(OrderRequest orderRequest) {
    checkAllItemsStock(orderRequest);
    return orderDbManager.saveOrderTx(orderRequest);
  }

  private void checkAllItemsStock(OrderRequest orderRequest) {
    stockServiceClient.verifyProductStocks(orderRequest.items());
  }
}
