package com.example.stock_service.processor;

import com.example.stock_service.dto.StockErrorResponse;
import com.example.stock_service.exception.InvalidStockException;
import com.example.stock_service.exception.StockNotAvailableException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("exceptionProcessor")
public class ExceptionProcessor implements Processor {
  @Override
  public void process(Exchange exchange) throws Exception {
    var ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, RuntimeException.class);
    var msg = ex.getMessage();
    String type =
        switch (ex) {
          case StockNotAvailableException e -> e.getClass().getSimpleName();
          case InvalidStockException e -> e.getClass().getSimpleName();
          default -> "StockUnknownException";
        };
    var errRes = new StockErrorResponse(type, "Bad Request", msg);
    exchange.getIn().setBody(errRes);
  }
}
