package com.example.stock_service.processor;

import com.example.stock_service.StockDto;
import com.example.stock_service.exception.StockNotAvailableException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("stockCheckProcessor") // Đăng ký nó như một Spring Bean với tên định danh
public class StockCheckProcessor implements Processor {
  Logger logger = LogManager.getLogger();
  @Override
  public void process(Exchange exchange) throws Exception {
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> rows = exchange.getIn().getBody(List.class);

    Map<Long, Integer> dbStockMap = rows.stream().collect(Collectors.toMap(
            row -> ((Number) row.get("id")).longValue(),
            row -> ((Number) row.get("quantity")).intValue()
    ));

    List<StockDto> requestStock = exchange.getProperty("requestStock", List.class);

    var isOutOfStock = requestStock.parallelStream().anyMatch(item -> item.getQuantity() > dbStockMap.get(item.getId()));
    if(isOutOfStock) {
      throw new StockNotAvailableException("Out of Stock");
    }
    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
  }
}
