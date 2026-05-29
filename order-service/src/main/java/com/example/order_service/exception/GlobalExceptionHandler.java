package com.example.order_service.exception;

import com.example.order_service.dto.OrderErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({StockNotAvailableException.class, InvalidStockException.class})
  public ResponseEntity<OrderErrorResponse> handleStockNotAvailable(RuntimeException ex) {
    var res = new OrderErrorResponse("Bad Request", ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(StockServiceException.class)
  public ResponseEntity<OrderErrorResponse> handleStockServiceError(StockServiceException ex) {
    var res = new OrderErrorResponse("Internal Server Error", ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
