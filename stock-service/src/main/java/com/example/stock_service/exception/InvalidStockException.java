package com.example.stock_service.exception;

public class InvalidStockException extends RuntimeException {
  public InvalidStockException(String message) {
    super(message);
  }
}
