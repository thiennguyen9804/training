package com.example.order_service.dto;

public record StockResponse(boolean available, int currentQuantity, String message) {}
