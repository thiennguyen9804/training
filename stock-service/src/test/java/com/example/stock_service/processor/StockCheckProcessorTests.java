package com.example.stock_service.processor;

import static org.junit.jupiter.api.Assertions.*;

import com.example.stock_service.dto.StockDto;
import com.example.stock_service.exception.StockNotAvailableException;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class StockCheckProcessorTests {

  private StockCheckProcessor processor;
  private CamelContext context;
  private Exchange exchange;

  @BeforeEach
  public void setUp() {
    processor = new StockCheckProcessor();
    context = new DefaultCamelContext();
    exchange = new DefaultExchange(context);
    StockDto mockDbDto1 = new StockDto(1L, 9999);
    StockDto mockDbDto2 = new StockDto(2L, 9999);
    var list = List.of(mockDbDto1, mockDbDto2);
    exchange.getIn().setBody(list);

  }

  @Test
  public void testProcess_WhenStockIsEnough_ShouldReturn200AndAvailableTrue() throws Exception {
    exchange.setProperty("requestStock", List.of(new StockDto(1L, 1)));

    processor.process(exchange);

    Integer statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
    assertEquals(200, statusCode);
  }

  @Test
  public void testProcess_WhenStockIsNotEnough_ShouldReturn400AndThrowStockNotAvailableException() throws Exception {
    exchange.setProperty("requestStock", List.of(new StockDto(1L, 10000)));

    var exception = assertThrowsExactly(
            StockNotAvailableException.class,
            () -> processor.process(exchange)
    );

    assertEquals("Out of Stock", exception.getMessage());


  }
}
