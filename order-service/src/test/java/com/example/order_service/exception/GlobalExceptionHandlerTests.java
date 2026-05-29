package com.example.order_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTests {
    private MockMvc mockMvc;
    @RestController
    static class TestController {
        @GetMapping("/test-stock-not-available")
        public void throwStockNotAvailable() {
            throw new StockNotAvailableException("Out of stock product");
        }

        @GetMapping("/test-stock-service-error")
        public void throwStockServiceError() {
            throw new StockServiceException("Connection timeout");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testHandleStockNotAvailable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/test-stock-not-available"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
