package com.example.stock_service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.stock_service.processor.StockCheckProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StockCheckProcessorTests {

  private StockCheckProcessor processor;
  private CamelContext context;

  @BeforeEach
  public void setUp() {
    // Khởi tạo đối tượng processor cần test
    processor = new StockCheckProcessor();
    // Tạo một CamelContext ảo phục vụ việc tạo Exchange
    context = new DefaultCamelContext();
  }

  @Test
  public void testProcess_WhenStockIsEnough_ShouldReturn200AndAvailableTrue() throws Exception {
    // --- 1. GIAI ĐOẠN CHUẨN BỊ (ARRANGE) ---
    Exchange exchange = new DefaultExchange(context);

    // Giả lập dữ liệu: Khách mua 5 cái, Trong kho có 10 cái
    exchange.getIn().setHeader("quantity", 5);
    exchange.getIn().setBody(10); // Kết quả giả định từ lệnh SQL trả về số 10

    // --- 2. GIAI ĐOẠN CHẠY THỬ (ACT) ---
    processor.process(exchange);

    // --- 3. GIAI ĐOẠN KIỂM TRA KẾT QUẢ (ASSERT) ---
    // Kiểm tra HTTP Status Code trả về phải là 200
    Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
    assertEquals(200, statusCode);

    // Kiểm tra Body trả về phải là Object StockResponse hợp lệ
    StockResponse response = exchange.getIn().getBody(StockResponse.class);
    assertNotNull(response);
    assertTrue(response.isAvailable());
    assertEquals(10, response.getCurrentQuantity());
    assertEquals("Đủ hàng!", response.getMessage());
  }

  @Test
  public void testProcess_WhenStockIsNotEnough_ShouldReturn400AndAvailableFalse() throws Exception {
    // --- 1. GIAI ĐOẠN CHUẨN BỊ (ARRANGE) ---
    Exchange exchange = new DefaultExchange(context);

    // Giả lập dữ liệu: Khách mua 10 cái, Trong kho chỉ còn 3 cái
    exchange.getIn().setHeader("quantity", 10);
    exchange.getIn().setBody(3); // Kết quả giả định từ lệnh SQL trả về số 3

    // --- 2. GIAI ĐOẠN CHẠY THỬ (ACT) ---
    processor.process(exchange);

    // --- 3. GIAI ĐOẠN KIỂM TRA KẾT QUẢ (ASSERT) ---
    // Kiểm tra HTTP Status Code trả về phải là 400
    Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
    assertEquals(400, statusCode);

    // Kiểm tra Body trả về báo không đủ hàng
    StockResponse response = exchange.getIn().getBody(StockResponse.class);
    assertNotNull(response);
    assertFalse(response.isAvailable());
    assertEquals(3, response.getCurrentQuantity());
    assertEquals("Hết hàng hoặc không đủ số lượng!", response.getMessage());
  }

  @Test
  public void testProcess_WhenProductNotFoundInDb_ShouldHandleNullAndReturn400() throws Exception {
    // --- 1. GIAI ĐOẠN CHUẨN BỊ (ARRANGE) ---
    Exchange exchange = new DefaultExchange(context);

    // Giả lập dữ liệu: Khách mua 1 cái, DB trả về NULL (Sản phẩm không tồn tại)
    exchange.getIn().setHeader("quantity", 1);
    exchange.getIn().setBody(null);

    // --- 2. GIAI ĐOẠN CHẠY THỬ (ACT) ---
    processor.process(exchange);

    // --- 3. GIAI ĐOẠN KIỂM TRA KẾT QUẢ (ASSERT) ---
    Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
    assertEquals(400, statusCode);

    StockResponse response = exchange.getIn().getBody(StockResponse.class);
    assertNotNull(response);
    assertFalse(response.isAvailable());
    assertEquals(0, response.getCurrentQuantity()); // Null được convert về số 0
  }
}
