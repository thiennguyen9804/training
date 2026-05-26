package com.example.order_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;

import com.example.order_service.client.StockServiceClient;
import com.example.order_service.dto.StockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class) // Kích hoạt tính năng Mockito cho JUnit 5
public class StockServiceClientTests {

  @Mock private RestTemplate restTemplate; // Giả lập RestTemplate

  @InjectMocks
  private StockServiceClient stockServiceClient; // Tự động tiêm RestTemplate giả vào đây

  private final String mockUrl =
      "http://localhost:8081/v1/stocks/check?productId={productId}&quantity={quantity}";

  @BeforeEach
  public void setUp() {
    // Gán giá trị thủ công cho biến @Value bằng Reflection vì không chạy qua Spring Context
    ReflectionTestUtils.setField(stockServiceClient, "checkStockUrlTemplate", mockUrl);
  }

  // --- KỊCH BẢN 1: ĐỦ HÀNG (Không ném ra bất kỳ lỗi nào) ---
  @Test
  public void verifyProductStock_WhenStockIsAvailable_ShouldPassSuccessfully() {
    // Chuẩn bị dữ liệu giả lập
    StockResponse mockResponse = new StockResponse(true, 10, "Đủ hàng!");

    // Định hình hành vi: Khi gọi restTemplate.getForObject với bất kỳ tham số nào, trả về
    // mockResponse
    when(restTemplate.getForObject(eq(mockUrl), eq(StockResponse.class), anyLong(), anyInt()))
        .thenReturn(mockResponse);

    // Chạy thử nghiệm và kiểm tra: Không được ném ra bất kỳ Exception nào
    assertDoesNotThrow(() -> stockServiceClient.verifyProductStock(1L, 5));

    // Kiểm tra xem RestTemplate thực sự đã được gọi đúng 1 lần duy nhất hay chưa
    verify(restTemplate, times(1))
        .getForObject(eq(mockUrl), eq(StockResponse.class), eq(1L), eq(5));
  }

  // --- KỊCH BẢN 2: THIẾU HÀNG / HẾT HÀNG (StockResponse trả về available = false) ---
  @Test
  public void verifyProductStock_WhenStockNotAvailable_ShouldThrowStockNotAvailableException() {
    StockResponse mockResponse = new StockResponse(false, 2, "Hết hàng hoặc không đủ số lượng!");

    when(restTemplate.getForObject(eq(mockUrl), eq(StockResponse.class), anyLong(), anyInt()))
        .thenReturn(mockResponse);

    // Kiểm tra xem hệ thống có ném ra đúng lỗi StockNotAvailableException hay không
    StockNotAvailableException exception =
        assertThrows(
            StockNotAvailableException.class,
            () -> {
              stockServiceClient.verifyProductStock(1L, 5);
            });

    // Kiểm tra thông điệp lỗi bên trong Exception
    assertEquals("Sản phẩm ID 1 không đủ hàng.", exception.getMessage());
  }

  // --- KỊCH BẢN 3: ĐÁP ỨNG TỪ CAMEL TRẢ VỀ LỖI NGHIỆP VỤ (HTTP 400 Bad Request) ---
  @Test
  public void
      verifyProductStock_WhenHttpClientErrorBadRequest_ShouldThrowStockNotAvailableException()
          throws Exception {
    String errorJson =
        "{\"available\":false,\"currentQuantity\":0,\"message\":\"Hết hàng hoàn toàn!\"}";

    // Tạo exception gốc rồi wrap bằng spy
    HttpClientErrorException.BadRequest badRequestException =
        Mockito.spy(
            (HttpClientErrorException.BadRequest)
                HttpClientErrorException.create(
                    HttpStatus.BAD_REQUEST,
                    "Bad Request",
                    null,
                    errorJson.getBytes(StandardCharsets.UTF_8),
                    null));

    // Mock getResponseBodyAs() trả về object đã parse sẵn
    StockResponse errorResponse = new StockResponse(false, 0, "Hết hàng hoàn toàn!");

    doReturn(errorResponse).when(badRequestException).getResponseBodyAs(StockResponse.class);

    // Giả lập RestTemplate ném ra ngoại lệ HTTP 400
    when(restTemplate.getForObject(
            eq(mockUrl), eq(StockResponse.class), any(Object.class), any(Object.class)))
        .thenThrow(badRequestException);

    StockNotAvailableException exception =
        assertThrows(
            StockNotAvailableException.class,
            () -> {
              stockServiceClient.verifyProductStock(1L, 5);
            });

    assertTrue(exception.getMessage().contains("Lỗi kiểm tra sản phẩm ID 1: Hết hàng hoàn toàn!"));
  }

  // --- KỊCH BẢN 4: LỖI KẾT NỐI HỆ THỐNG (Sập mạng, Timeout, Lỗi 500...) ---
  @Test
  public void verifyProductStock_WhenGenericException_ShouldThrowStockServiceException() {
    // Giả lập một lỗi kết nối kỹ thuật bất kỳ (ví dụ RuntimeException mạng sập)
    RuntimeException networkError = new RuntimeException("Connection refused");

    when(restTemplate.getForObject(eq(mockUrl), eq(StockResponse.class), anyLong(), anyInt()))
        .thenThrow(networkError);

    // Hệ thống phải ném ra lỗi kỹ thuật StockServiceException
    StockServiceException exception =
        assertThrows(
            StockServiceException.class,
            () -> {
              stockServiceClient.verifyProductStock(1L, 5);
            });

    assertTrue(
        exception.getMessage().contains("Lỗi kết nối hệ thống kho khi kiểm tra sản phẩm ID 1"));
    assertEquals(
        networkError,
        exception.getCause()); // Đảm bảo giữ được nguyên nhân gốc (cause) để debug log
  }
}
