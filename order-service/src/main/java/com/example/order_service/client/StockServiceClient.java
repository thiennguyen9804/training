package com.example.order_service.client;

import com.example.order_service.StockNotAvailableException;
import com.example.order_service.StockServiceException;
import com.example.order_service.dto.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class StockServiceClient {

  private final RestTemplate restTemplate;

  @Value("${app.api.check-stock-url}")
  private String checkStockUrlTemplate;

  /** Hàm chuyên trách kiểm tra kho cho duy nhất 1 sản phẩm */
  public void verifyProductStock(Long productId, int quantity) {
    try {
      StockResponse stock =
          restTemplate.getForObject(
              checkStockUrlTemplate, StockResponse.class, productId, quantity);

      if (stock == null || !stock.available()) {
        throw new StockNotAvailableException("Sản phẩm ID " + productId + " không đủ hàng.");
      }
    } catch (HttpClientErrorException.BadRequest ex) {
      StockResponse errorResponse = ex.getResponseBodyAs(StockResponse.class);
      String msg = (errorResponse != null) ? errorResponse.message() : "Hết hàng";
      throw new StockNotAvailableException("Lỗi kiểm tra sản phẩm ID " + productId + ": " + msg);
    } catch (StockNotAvailableException ex) {
      throw ex;
    } catch (Exception e) {
      throw new StockServiceException(
          "Lỗi kết nối hệ thống kho khi kiểm tra sản phẩm ID " + productId + ": " + e.getMessage(),
          e);
    }
  }
}
