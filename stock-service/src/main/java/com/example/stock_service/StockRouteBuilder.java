package com.example.stock_service;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

/** A Camel Java DSL Router */
@Component
public class StockRouteBuilder extends RouteBuilder {

  /** Let's configure the Camel routing rules using Java code... */
  public void configure() {
    restConfiguration().bindingMode(RestBindingMode.json);
    rest("/v1/stocks/")
        .get("/check")
        .param()
        .name("productId")
        .type(RestParamType.query)
        .required(true)
        .description("ID của sản phẩm")
        .endParam()
        .param()
        .name("quantity")
        .type(RestParamType.query)
        .required(true)
        .description("Số lượng cần check")
        .endParam()
        .to("direct:checkStocks");

    from("direct:checkStocks")
        .to(
            "sql:select quantity from stocks where id = :#productId?outputType=SelectOne&dataSource=#dataSource")
        .process("stockCheckProcessor");
  }
}
