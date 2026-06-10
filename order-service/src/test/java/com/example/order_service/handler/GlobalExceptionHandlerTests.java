package com.example.order_service.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.order_service.model.exception.StockNotAvailableException;
import com.example.order_service.model.exception.StockServiceException;
import com.example.order_service.model.exception.UnauthenticatedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

public class GlobalExceptionHandlerTests {
  private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Test
  void testHandleStockNotAvailable_ReturnsBadRequest() {
    StockNotAvailableException ex = new StockNotAvailableException("Out of stock product");
    var res = globalExceptionHandler.handleStockNotAvailable(ex);
    assertEquals(HttpStatusCode.valueOf(400), res.getStatusCode());
  }

  @Test
  void testHandleStockServiceError_ReturnsInternalServiceError() {
    StockServiceException ex = new StockServiceException("Out of stock product");
    var res = globalExceptionHandler.handleStockServiceError(ex);
    assertEquals(HttpStatusCode.valueOf(500), res.getStatusCode());
  }

  @Test
  void testEmptyJWT_ReturnsUnauthenticatedError() {
    UnauthenticatedException ex = new UnauthenticatedException("");
    var res = globalExceptionHandler.handleUnauthenticated(ex);
    assertEquals(HttpStatusCode.valueOf(401), res.getStatusCode());
  }
}
