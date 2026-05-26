package com.example.order_service.dto;

import java.util.List;

public class OrderRequest {
  private Long customerId;
  private List<ItemDto> items;

  // Getter và Setter
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public List<ItemDto> getItems() {
    return items;
  }

  public void setItems(List<ItemDto> items) {
    this.items = items;
  }

  public static class ItemDto {
    private Long productId;
    private int quantity;

    // Getter và Setter
    public Long getProductId() {
      return productId;
    }

    public void setProductId(Long productId) {
      this.productId = productId;
    }

    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
    }
  }
}
