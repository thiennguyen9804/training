package com.example.order_service.exception;

public class StockNotAvailableException extends RuntimeException {
  public StockNotAvailableException(String message) {
    super(message);
  }
}
