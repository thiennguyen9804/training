package com.example.order_service;

public class StockServiceException extends RuntimeException {
  public StockServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
