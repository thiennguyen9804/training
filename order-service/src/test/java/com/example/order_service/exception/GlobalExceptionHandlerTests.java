package com.example.order_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTests {
  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Test
  void testHandleStockNotAvailable_ReturnsBadRequest() {
    StockNotAvailableException ex = new StockNotAvailableException("Out of stock product");
    var res = globalExceptionHandler.handleStockNotAvailable(ex);
    assertEquals(HttpStatusCode.valueOf(400), res.getStatusCode());
  }

  @Test
  void testHandleStockServiceError_ReturnsBadRequest() {
    StockServiceException ex = new StockServiceException("Out of stock product");
    var res = globalExceptionHandler.handleStockServiceError(ex);
    assertEquals(HttpStatusCode.valueOf(500), res.getStatusCode());
  }
}
