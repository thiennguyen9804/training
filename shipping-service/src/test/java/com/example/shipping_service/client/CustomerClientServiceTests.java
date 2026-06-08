package com.example.shipping_service.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerClientServiceTests {
    private final CustomerClientService customerClientService = new CustomerClientService();

    @Test
    void shouldReturnCorrectCustomerAddress() {
        Long customerId = 123L;
        String expectedAddress = "Floor 4, KMS Technology, Ho Chi Minh City, Vietnam";
        String actualAddress = customerClientService.getCustomerAddress(customerId);
        assertEquals(expectedAddress, actualAddress);
    }
}
