package com.example.order_service.model.exception;

public class InvalidStockException extends RuntimeException {
  public InvalidStockException(String message) {
    super(message);
  }
}
