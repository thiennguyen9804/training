package com.example.stock_service.processor;

import com.example.stock_service.dto.QueriedOutbox;
import com.example.stock_service.dto.UpdateShippingDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("updateShippingPayloadMapper")
public class UpdateShippingPayloadMapper implements Processor {
  private final ObjectMapper mapper = new ObjectMapper();

  public List<QueriedOutbox> toDtoList(List<Map<String, Object>> rows) {
    List<QueriedOutbox> outboxList = new ArrayList<>();
    if (rows != null) {
      for (Map<String, Object> row : rows) {
        String jsonStr = (String) row.get("payload");
        UpdateShippingDto dto = null;
        Long id = (Long) row.get("id");
        try {
          dto = mapper.readValue(jsonStr, UpdateShippingDto.class);
        } catch (JsonProcessingException ignored) {
        }
        QueriedOutbox outbox = new QueriedOutbox();
        outbox.setId(id);
        outbox.setPayload(dto);
        outboxList.add(outbox);
      }
    }
    return outboxList;
  }

  @Override
  public void process(Exchange exchange) {
    List<Map<String, Object>> dbRows = exchange.getIn().getBody(List.class);
    var res = toDtoList(dbRows);
    exchange.getIn().setBody(res);
  }
}
