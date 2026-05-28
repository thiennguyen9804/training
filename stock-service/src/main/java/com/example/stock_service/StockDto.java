package com.example.stock_service;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Bắt buộc phải có để Camel khởi tạo object
public class StockDto {
  private Long id;
  private int quantity;
}
