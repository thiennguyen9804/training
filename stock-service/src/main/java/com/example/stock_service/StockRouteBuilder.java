package com.example.stock_service;

import com.example.stock_service.exception.InvalidStockException;
import com.example.stock_service.exception.StockNotAvailableException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/** A Camel Java DSL Router */
@Component
public class StockRouteBuilder extends RouteBuilder {
  private final Logger logger = LogManager.getLogger();

  public void configure() {
    restConfiguration().bindingMode(RestBindingMode.json);
      onException(InvalidStockException.class, StockNotAvailableException.class)
              .handled(true)
              .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
              .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                      .process(exchange -> {
                            var ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, RuntimeException.class);
                            var msg = ex.getMessage();
                            String type = switch(ex) {
                                case StockNotAvailableException e -> e.getClass().getSimpleName();
                                case InvalidStockException e ->  e.getClass().getSimpleName();
                                default -> "StockUnknownException";
                            };
                            var errRes = new StockErrorResponse(type, "Bad Request", msg);
                            exchange.getIn().setBody(errRes);
                      });


    rest("/v1/stocks/")
            .post("/check")
            .type(StockDto[].class)
            .to("direct:checkStocks");

    from("direct:checkStocks")
            .log("Body type before marshaling: ${body.class}, Body: ${body}")
            .marshal().json()
            .choice()
            .when(jsonpath("$[?(@.quantity < 0)]"))
            .throwException(new InvalidStockException("Quantity cannot be negative!!!"))
            .end()
        .log("Body type before unmarshaling: ${body.class}, Body: ${body}")
        .unmarshal().json(StockDto[].class)
        .setProperty("requestStock", body())
        .process("mapperProcessor")
        .to(
            "sql:SELECT * FROM stocks WHERE id IN (:#in:${body})?outputClass=com.example.stock_service.StockDto")
        .process("stockCheckProcessor");
  }
}
