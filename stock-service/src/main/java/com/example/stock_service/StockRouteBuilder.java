package com.example.stock_service;

import com.example.stock_service.exception.InvalidStockException;
import com.example.stock_service.exception.StockNotAvailableException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

/** A Camel Java DSL Router */
@Component
public class StockRouteBuilder extends RouteBuilder {
  private final Logger logger = LogManager.getLogger();

  public void configure() {
    restConfiguration().bindingMode(RestBindingMode.json);
    onException(InvalidStockException.class)
        .handled(true)
        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
        .setBody(
            simple(
                """
        {
            "type": "InvalidStockException",
            "error": "Bad Request",
            "message": "${exception.message}"
        }
    """));
onException(StockNotAvailableException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setBody(
                    simple(
                            """
                    {
                        "type": "StockNotAvailableException",
                        "error": "Bad Request",
                        "message": "${exception.message}"
                    }
                """)
            );

    rest("/v1/stocks/")
        .post("/check")
            .type(StockDto[].class)
        .to("direct:checkStocks");

    from("direct:checkStocks")
            .setProperty("requestStock", body())
            .process("mapperProcessor")
            .to("sql:SELECT * FROM stocks WHERE id IN (:#in:${body})?outputClass=com.example.stock_service.StockDto")
            .process(exchange -> {
              var results = exchange.getIn().getBody(List.class);
              logger.info("List of id: {}", results);
            });

  }
}
