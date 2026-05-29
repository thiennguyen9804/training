package com.example.stock_service;

import com.example.stock_service.dto.StockDto;
import com.example.stock_service.exception.InvalidStockException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/** A Camel Java DSL Router */
@Component
public class StockRouteBuilder extends RouteBuilder {
  private final Logger logger = LogManager.getLogger();

  public void configure() {
    rest("/v1/stocks/")
            .post("/check")
            .type(StockDto[].class)
            .to("direct:checkStocks")
            .get("/check-logger")
            .to("direct:printLogger");
    from("direct:printLogger")
            .process(exchange -> {
                String loggerClass = org.slf4j.LoggerFactory.getLogger("CamelLogger").getClass().getName();

                // In ra console của ứng dụng
                System.out.println(">>> Logger implementation đang dùng: " + loggerClass);

                // Trả về tên class logger làm phản hồi HTTP cho API
                exchange.getIn().setBody("Logger implementation: " + loggerClass);

            });

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
            "sql:SELECT * FROM stocks WHERE id IN (:#in:${body})?outputClass=com.example.stock_service.dto.StockDto")
        .process("stockCheckProcessor");
  }
}
