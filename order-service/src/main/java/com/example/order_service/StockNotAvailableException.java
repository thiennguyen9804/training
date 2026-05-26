package com.example.order_service;

public class StockNotAvailableException extends RuntimeException {
  public StockNotAvailableException(String message) {
    super(message);
  }
}
