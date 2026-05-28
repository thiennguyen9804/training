package com.example.order_service.dto;

public record OrderErrorResponse(
        String error,
        String message
) {

}
