package com.example.order_service.manager;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDbManager {

  private final OrderRepository orderRepository;

  @Transactional
  public Order saveOrderTx(OrderRequest orderRequest) {
    Order newOrder = new Order();
    newOrder.setCustomerId(orderRequest.customerId());

    for (OrderRequest.ItemDto itemDto : orderRequest.items()) {
      OrderItem dbItem = new OrderItem(itemDto.id(), itemDto.quantity());
      newOrder.addOrderItem(dbItem);
    }
    return orderRepository.save(newOrder);
  }


}
