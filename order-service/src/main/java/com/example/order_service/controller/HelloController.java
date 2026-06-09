package com.example.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/hello")
@RequiredArgsConstructor
public class HelloController {
    @GetMapping("/")
    public String sayHello() {
        return "Hello from Order Service";
    }
}
