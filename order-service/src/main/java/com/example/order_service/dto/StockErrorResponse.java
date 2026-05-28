package com.example.order_service.dto;

import lombok.NonNull;

public record StockErrorResponse(
String type,
String error,
String message

) {}
// available is true iff each product has sufficient quantity