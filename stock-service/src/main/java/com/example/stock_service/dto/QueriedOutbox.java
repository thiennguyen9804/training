package com.example.stock_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueriedOutbox {
  private Long id;
  private UpdateShippingDto payload;
}
