package com.example.stock_service;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Value("${camel.servlet.base-api}")
  String contextPath;

  @Bean
  ServletRegistrationBean<?> servletRegistrationBean() {
    ServletRegistrationBean<?> servlet =
        new ServletRegistrationBean<>(new CamelHttpTransportServlet(), contextPath + "/*");
    servlet.setName("CamelServlet");
    return servlet;
  }
}
