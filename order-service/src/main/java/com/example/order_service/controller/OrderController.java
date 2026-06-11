package com.example.order_service.controller;

import com.example.order_service.model.dto.OrderRequest;
import com.example.order_service.model.entity.Order;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
    Order savedOrder = orderService.createOrder(orderRequest);
    return ResponseEntity.ok("Your order is created successfully with id: " + savedOrder.getId());
  }
}
