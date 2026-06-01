package com.example.order_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.order_service.client.StockServiceClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.manager.OrderDbManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

  @Mock private StockServiceClient stockServiceClient;
  @Mock private OrderDbManager manager;
  @InjectMocks private OrderService orderService;
  private OrderRequest sampleRequest;
  private Order order;

  @BeforeEach
  public void setUp() {
    OrderRequest.ItemDto item1 = new OrderRequest.ItemDto(101L, 2);
    OrderRequest.ItemDto item2 = new OrderRequest.ItemDto(102L, 1);
    sampleRequest = new OrderRequest(1L, List.of(item1, item2));
    order = new Order();
    order.setId(1L);
  }

  @Test
  public void createOrder_WhenAllItemsAvailable_ShouldSaveOrderSuccessfully() {
    doReturn(order).when(manager).saveOrderTx(sampleRequest);
    Order savedOrder = orderService.createOrder(sampleRequest);
    verify(stockServiceClient, times(1)).verifyProductStocks(sampleRequest.items());
    verify(manager, times(1)).saveOrderTx(sampleRequest);
    assertEquals(1L, savedOrder.getId());
  }
}
