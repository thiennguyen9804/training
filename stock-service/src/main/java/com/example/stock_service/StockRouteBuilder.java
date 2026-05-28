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
    onException(InvalidStockException.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))

            .setBody(simple("new com.example.stock_service.StockErrorResponse('InvalidStockException', 'Bad Request', ${exception.message})"))
    ;
    onException(InvalidStockException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> {
              String msg = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, InvalidStockException.class).getMessage();
              exchange.getMessage().setBody(new StockErrorResponse("InvalidStockException", "Bad Request", msg));
            });

    onException(StockNotAvailableException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .process(exchange -> {
              String msg = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, StockNotAvailableException.class).getMessage();
              exchange.getMessage().setBody(new StockErrorResponse("StockNotAvailableException", "Bad Request", msg));
            });

    rest("/v1/stocks/")
            .post("/check")
            .type(StockDto[].class)
            .to("direct:checkStocks");

    from("direct:checkStocks")
            .marshal().json()
            .choice()
            .when(jsonpath("$[?(@.quantity < 0)]"))
            .throwException(new InvalidStockException("Quantity cannot be negative!!!"))
            .end()
        .unmarshal().json(StockDto[].class)
        .setProperty("requestStock", body())
        .process("mapperProcessor")
        .to(
            "sql:SELECT * FROM stocks WHERE id IN (:#in:${body})?outputClass=com.example.stock_service.StockDto")
        .process("stockCheckProcessor");
  }
}
