package com.example.order_service.model.exception;

public class StockServiceException extends RuntimeException {
  public StockServiceException(String message) {
    super(message);
  }
}
