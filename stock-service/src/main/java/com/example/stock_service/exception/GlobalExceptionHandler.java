package com.example.stock_service.exception;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration().bindingMode(RestBindingMode.json);
        onException(InvalidStockException.class, StockNotAvailableException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .process("exceptionProcessor");
    }
}
