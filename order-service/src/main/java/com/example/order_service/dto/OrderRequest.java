package com.example.order_service.dto;

import lombok.Data;

import java.util.List;

public record OrderRequest(
        Long customerId,
        List<OrderRequest.ItemDto> items
) {
  public record ItemDto(
          Long productId,
          int quantity
  ) {

  }
}