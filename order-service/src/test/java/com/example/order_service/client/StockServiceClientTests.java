package com.example.order_service.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.dto.StockErrorResponse;
import com.example.order_service.exception.InvalidStockException;
import com.example.order_service.exception.StockNotAvailableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class StockServiceClientTests {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private StockServiceClient stockServiceClient;

  private final ObjectMapper objectMapper = new ObjectMapper();
  OrderRequest.ItemDto item = new OrderRequest.ItemDto(101L, 2);
  private final List<OrderRequest.ItemDto> items = List.of(item);

  @Test
  public void verifyProductStock_WhenStockIsAvailable_ShouldPassSuccessfully() {
    var items = List.of(new OrderRequest.ItemDto(1L, 5));
    assertDoesNotThrow(() -> stockServiceClient.verifyProductStocks(items));

    String mockUrl = "/check";
    verify(restTemplate, times(1)).postForObject(eq(mockUrl), any(), eq(Void.class));
  }

  @Test
  public void
      verifyProductStock_WhenStockServiceReturnErrorWithStockNotAvailableException_ShouldThrowStockNotAvailableException()
          throws JsonProcessingException {
    StockErrorResponse mockErrorBody = new StockErrorResponse("StockNotAvailableException", "", "");
    String jsonError = objectMapper.writeValueAsString(mockErrorBody);

    HttpClientErrorException.BadRequest badRequestException =
        (HttpClientErrorException.BadRequest)
            HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                new HttpHeaders(),
                jsonError.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
    var spyException = spy(badRequestException);
    doReturn(mockErrorBody).when(spyException).getResponseBodyAs(StockErrorResponse.class);
    when(restTemplate.postForObject(eq("/check"), anyList(), eq(Void.class)))
        .thenThrow(spyException);
    assertThrows(
        StockNotAvailableException.class, () -> stockServiceClient.verifyProductStocks(items));
  }

  // --- KỊCH BẢN 3: ĐÁP ỨNG TỪ CAMEL TRẢ VỀ LỖI NGHIỆP VỤ (HTTP 400 Bad Request) ---
  @Test
  public void
      verifyProductStock_WhenStockServiceReturnErrorWithInvalidStockException_ShouldThrowInvalidStockException()
          throws JsonProcessingException {
    StockErrorResponse mockErrorBody = new StockErrorResponse("InvalidStockException", "", "");
    String jsonError = objectMapper.writeValueAsString(mockErrorBody);

    HttpClientErrorException.BadRequest badRequestException =
        (HttpClientErrorException.BadRequest)
            HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                new HttpHeaders(),
                jsonError.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8);
    var spyException = spy(badRequestException);
    doReturn(mockErrorBody).when(spyException).getResponseBodyAs(StockErrorResponse.class);
    when(restTemplate.postForObject(eq("/check"), anyList(), eq(Void.class)))
        .thenThrow(spyException);
    assertThrows(InvalidStockException.class, () -> stockServiceClient.verifyProductStocks(items));
  }
}
