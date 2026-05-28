package com.example.stock_service.processor;

import com.example.stock_service.StockDto;
import com.example.stock_service.exception.StockNotAvailableException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("stockCheckProcessor") // Đăng ký nó như một Spring Bean với tên định danh
public class StockCheckProcessor implements Processor {
  Logger logger = LogManager.getLogger();

  @Override
  public void process(Exchange exchange) throws Exception {
    @SuppressWarnings("unchecked")
    List<StockDto> rows = exchange.getIn().getBody(List.class);

    Map<Long, Integer> dbStockMap =
        rows.stream()
            .collect(
                Collectors.toMap(
                    StockDto::getId, // Lấy ID trực tiếp từ đối tượng
                    StockDto::getQuantity // Lấy số lượng trực tiếp từ đối tượng
                    ));

    List<StockDto> requestStock = exchange.getProperty("requestStock", List.class);

    boolean isOutOfStock =
        requestStock.parallelStream()
            .anyMatch(
                item -> {
                  Integer dbQuantity = dbStockMap.get(item.getId());

                  // Nếu sản phẩm không tồn tại trong DB, coi như số lượng trong DB bằng 0
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
