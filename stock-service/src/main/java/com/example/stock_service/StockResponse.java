package com.example.stock_service;

public class StockResponse {
  private boolean available;
  private int currentQuantity;
  private String message;

  // Các Constructor
  public StockResponse() {}

  public StockResponse(boolean available, int currentQuantity, String message) {
    this.available = available;
    this.currentQuantity = currentQuantity;
    this.message = message;
  }

  // Bắt buộc phải có đầy đủ Getter và Setter để Jackson tự chuyển sang JSON
  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public int getCurrentQuantity() {
    return currentQuantity;
  }

  public void setCurrentQuantity(int currentQuantity) {
    this.currentQuantity = currentQuantity;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
