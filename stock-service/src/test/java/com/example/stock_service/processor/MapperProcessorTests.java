package com.example.stock_service.processor;

import static org.junit.jupiter.api.Assertions.*;

import com.example.stock_service.dto.StockDto;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MapperProcessorTests {
  private MapperProcessor processor;
  private Exchange exchange;

  @BeforeEach
  public void setUp() {
    processor = new MapperProcessor();
    CamelContext context = new DefaultCamelContext();
    exchange = new DefaultExchange(context);
    StockDto mockRequestDto1 = new StockDto(1L, 9999);
    StockDto mockRequestDto2 = new StockDto(2L, 9999);
    var list = List.of(mockRequestDto1, mockRequestDto2);
    exchange.getIn().setBody(list);
  }

  @Test
  public void testProcess_WhenReceiveListOfDto_ShouldMapToListOfIds() throws Exception {
    processor.process(exchange);
    var idList = exchange.getIn().getBody();
    assertEquals(idList, List.of(1L, 2L));
  }
}
