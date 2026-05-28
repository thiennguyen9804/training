package com.example.order_service.exception;

public class StockServiceException extends RuntimeException {
  public StockServiceException(String message) {
    super(message);
  }
}
