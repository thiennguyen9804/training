package com.example.order_service.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.order_service.model.dto.OrderRequest;
import com.example.order_service.model.entity.Order;
import com.example.order_service.security.JwtService;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTests {
  @Autowired private MockMvc mockMvc;
  @MockitoBean private OrderService orderService;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private JwtService jwtService;

  private OrderRequest sampleRequest;

  @BeforeEach
  public void setUp() {
    OrderRequest.ItemDto item = new OrderRequest.ItemDto(101L, 2);
    sampleRequest = new OrderRequest(99L, List.of(item));
  }

  @Test
  public void createOrder_WhenRequestIsValid_ShouldReturn200AndSuccessMessage() throws Exception {
    Order mockSavedOrder = new Order();
    mockSavedOrder.setId(12345L);
    mockSavedOrder.setCustomerId(99L);

    when(orderService.createOrder(any(OrderRequest.class))).thenReturn(mockSavedOrder);

    ResultActions resultActions =
        mockMvc.perform(
            post("/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)));
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(
        content().string(containsString("Your order is created successfully with id:")));

    verify(orderService).createOrder(any(OrderRequest.class));
  }
}
