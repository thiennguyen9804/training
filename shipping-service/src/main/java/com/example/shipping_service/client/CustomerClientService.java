package com.example.shipping_service.client;

import org.springframework.stereotype.Component;

@Component
public class CustomerClientService {
    public String getCustomerAddress(Long customerId) {
        return "Floor 4, KMS Technology, Ho Chi Minh City, Vietnam";
    }
}
