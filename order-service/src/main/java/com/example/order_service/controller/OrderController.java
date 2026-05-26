package com.example.order_service.controller;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService; // 🚀 Chỉ cần tiêm (Inject) duy nhất Service vào đây

  @PostMapping
  public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
    System.out.println("Nhận request tại Controller: " + orderRequest);

    // Gọi Service xử lý trọn gói logic bên dưới
    Order savedOrder = orderService.createOrder(orderRequest);

    // Trả về kết quả thành công cho Client
    return ResponseEntity.ok(
        "Tạo đơn hàng thành công! Mã đơn hàng của bạn là: " + savedOrder.getId());
  }
}
