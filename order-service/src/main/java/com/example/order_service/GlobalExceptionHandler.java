package com.example.order_service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 🚀 Đánh dấu đây là bộ xử lý ngoại lệ tập trung toàn ứng dụng
public class GlobalExceptionHandler {

  // 1. Hứng lỗi khi Stock Service trả về lỗi 400 (Hết hàng/Thiếu hàng)
  @ExceptionHandler(StockNotAvailableException.class)
  public ResponseEntity<Map<String, Object>> handleStockNotAvailable(
      StockNotAvailableException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put("message", ex.getMessage()); // Câu báo lỗi kèm Product ID sẽ hiện ở đây

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  // 2. Hứng lỗi khi mất kết nối, sập mạng, lỗi timeout từ Stock Service
  @ExceptionHandler(StockServiceException.class)
  public ResponseEntity<Map<String, Object>> handleStockServiceError(StockServiceException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());

    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
