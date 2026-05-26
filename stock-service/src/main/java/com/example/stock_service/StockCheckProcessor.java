package com.example.stock_service;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("stockCheckProcessor") // Đăng ký nó như một Spring Bean với tên định danh
public class StockCheckProcessor implements Processor {

  @Override
  public void process(Exchange exchange) throws Exception {
    // 1. Lấy số lượng khách cần mua từ Header
    int requestedQty = exchange.getIn().getHeader("quantity", Integer.class);

    // 2. Lấy số lượng tồn kho thực tế từ DB (đang nằm ở Body sau lệnh SQL)
    Integer currentQty = exchange.getIn().getBody(Integer.class);
    if (currentQty == null) currentQty = 0;

    // 3. Khởi tạo Object Response
    StockResponse response = new StockResponse();
    response.setCurrentQuantity(currentQty);

    // 4. So sánh và ra quyết định
    if (currentQty >= requestedQty) {
      response.setAvailable(true);
      response.setMessage("Đủ hàng!");
      exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
    } else {
      response.setAvailable(false);
      response.setMessage("Hết hàng hoặc không đủ số lượng!");
      exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
    }

    // 5. Đẩy kết quả vào Body
    exchange.getIn().setBody(response);
  }
}
