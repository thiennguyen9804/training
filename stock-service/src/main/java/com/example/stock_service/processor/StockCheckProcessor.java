package com.example.stock_service.processor;

import com.example.stock_service.dto.StockDto;
import com.example.stock_service.exception.StockNotAvailableException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("stockCheckProcessor")
public class StockCheckProcessor implements Processor {
  @Override
  public void process(Exchange exchange) throws Exception {
    @SuppressWarnings("unchecked")
    List<StockDto> rows = exchange.getIn().getBody(List.class);

    Map<Long, Integer> dbStockMap =
        rows.stream()
            .collect(
                Collectors.toMap(
                    StockDto::getId,
                    StockDto::getQuantity
                    ));

    @SuppressWarnings("unchecked")
    List<StockDto> requestStock = exchange.getProperty("requestStock", List.class);
    boolean isOutOfStock =
        requestStock.parallelStream()
            .anyMatch(
                item -> {
                  Integer dbQuantity = dbStockMap.get(item.getId());
                  int availableStock = (dbQuantity != null) ? dbQuantity : 0;
                  return item.getQuantity() > availableStock;
                });
    if (isOutOfStock) {
      throw new StockNotAvailableException("Out of Stock");
    } else {
      exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
      exchange.getIn().setBody(null);
    }
  }
}
