package com.example.order_service.model.exception;

public class UnauthenticatedException extends Exception {
  public UnauthenticatedException(String message) {
    super(message);
  }
}
