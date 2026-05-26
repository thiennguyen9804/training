package com.example.order_service.service;

import com.example.order_service.repository.OrderRepository;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.client.StockServiceClient;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final StockServiceClient stockServiceClient;
  private final OrderRepository orderRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Transactional
  public Order createOrder(OrderRequest orderRequest) {

    // 1. Kiểm tra kho toàn bộ danh sách sản phẩm
    checkAllItemsStock(orderRequest);

    // 2. Tạo đối tượng và lưu xuống Database
    return saveOrderToDb(orderRequest);
  }

  /** Hàm nhỏ 1: Chuyên xử lý vòng lặp duyệt kho */
  private void checkAllItemsStock(OrderRequest orderRequest) {
    for (OrderRequest.ItemDto item : orderRequest.getItems()) {
      stockServiceClient.verifyProductStock(item.getProductId(), item.getQuantity());
    }
  }

  /** Hàm nhỏ 2: Chuyên xử lý ánh xạ (Mapping) dữ liệu và lưu DB */
  private Order saveOrderToDb(OrderRequest orderRequest) {
    Order newOrder = new Order();
    newOrder.setCustomerId(orderRequest.getCustomerId());

    for (OrderRequest.ItemDto itemDto : orderRequest.getItems()) {
      OrderItem dbItem = new OrderItem(itemDto.getProductId(), itemDto.getQuantity());
      newOrder.addOrderItem(dbItem);
    }

    return orderRepository.save(newOrder);
  }
}
