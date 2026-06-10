package com.example.order_service.model.dto;

import java.util.List;

public record OrderRequest(Long customerId, List<OrderRequest.ItemDto> items) {
  public record ItemDto(Long id, int quantity) {}
}
