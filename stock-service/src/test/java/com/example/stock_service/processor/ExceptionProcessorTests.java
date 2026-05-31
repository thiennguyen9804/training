package com.example.stock_service.processor;

import com.example.stock_service.dto.StockErrorResponse;
import com.example.stock_service.exception.StockNotAvailableException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionProcessorTests {
    private ExceptionProcessor exceptionProcessor;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        exceptionProcessor = new ExceptionProcessor();
        exchange = new DefaultExchange(new DefaultCamelContext());
    }

    @Test
    void testProcess_WhenStockNotAvailableException_ShouldReturnCorrectErrorResponse() throws Exception {
        String errorMessage = "Sản phẩm không đủ số lượng trong kho";
        StockNotAvailableException exception = new StockNotAvailableException("");
        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exception);
        exceptionProcessor.process(exchange);
        Message message = exchange.getIn();
        StockErrorResponse body = message.getBody(StockErrorResponse.class);
        assertNotNull(body);
        assertEquals("StockNotAvailableException", body.type());
        assertEquals("Bad Request", body.error());
    }
}
