package com.example.order_service.client;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.StockErrorResponse;
import com.example.order_service.exception.InvalidStockException;
import com.example.order_service.exception.StockNotAvailableException;
import com.example.order_service.exception.StockServiceException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class StockServiceClient {

  private final RestTemplate restTemplate;

  public void verifyProductStocks(List<OrderRequest.ItemDto> items) {
    String checkStocksUrl = "/check";
    try {
      restTemplate.postForObject(checkStocksUrl, items, Void.class);
    } catch (HttpClientErrorException.BadRequest ex) {
      var stockErr = ex.getResponseBodyAs(StockErrorResponse.class);
      throw switch (Objects.requireNonNull(stockErr).type()) {
        case "InvalidStockException" -> new InvalidStockException(stockErr.message());
        case "StockNotAvailableException" -> new StockNotAvailableException(stockErr.message());
        default -> new RuntimeException("Stock Verify failed with message: " + stockErr.message());
      };
    } catch (HttpServerErrorException ex) {
      throw new StockServiceException("Stock Service is currently down...");
    }
  }
}
