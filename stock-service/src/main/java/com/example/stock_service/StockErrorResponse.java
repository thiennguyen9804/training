package com.example.stock_service;

public record StockErrorResponse(
        String type,
        String error,
        String message

) {}
