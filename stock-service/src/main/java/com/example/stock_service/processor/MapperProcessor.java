package com.example.stock_service.processor;


import com.example.stock_service.StockDto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component("mapperProcessor")
public class MapperProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        StockDto[] items = exchange.getIn().getBody(StockDto[].class);
        var idList = Arrays.stream(items).map(StockDto::getId).toList();
        exchange.getIn().setBody(idList);
    }
}
