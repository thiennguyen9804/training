package com.example.order_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.order_service.controller.OrderController;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class) // 🚀 Chỉ định test riêng cho OrderController
public class OrderControllerTests {

  @Autowired private MockMvc mockMvc; // Công cụ giả lập gọi API HTTP

  @MockitoBean private OrderService orderService; // Giả lập (Mock) tầng Service phụ thuộc

  @Autowired
  private ObjectMapper objectMapper; // Công cụ của Spring để biến Object thành chuỗi JSON

  private OrderRequest sampleRequest;

  @BeforeEach
  public void setUp() {
    // Chuẩn bị dữ liệu request mẫu giống như Client gửi lên
    sampleRequest = new OrderRequest();
    sampleRequest.setCustomerId(99L);

    OrderRequest.ItemDto item = new OrderRequest.ItemDto();
    item.setProductId(101L);
    item.setQuantity(2);

    sampleRequest.setItems(Collections.singletonList(item));
  }

  // --- KỊCH BẢN: TẠO ĐƠN HÀNG THÀNH CÔNG ---
  @Test
  public void createOrder_WhenRequestIsValid_ShouldReturn200AndSuccessMessage() throws Exception {
    // 1. Arrange: Định hình hành vi cho OrderService giả lập
    Order mockSavedOrder = new Order();
    mockSavedOrder.setId(12345L); // Giả lập đơn hàng lưu thành công có ID là 12345
    mockSavedOrder.setCustomerId(99L);

    when(orderService.createOrder(any(OrderRequest.class))).thenReturn(mockSavedOrder);

    // 2. Act & Assert: Giả lập cú click chuột/gọi API từ Client và kiểm tra kết quả trả về
    mockMvc
        .perform(
            post("/v1/order") // Gửi một request dạng POST vào đúng endpoint
                .contentType(MediaType.APPLICATION_JSON) // Khai báo Header Content-Type
                .content(objectMapper.writeValueAsString(sampleRequest))) // Bắn Body JSON lên
        .andExpect(status().isOk()) // Kiểm tra HTTP Status trả về phải là 200 OK
        .andExpect(
            content()
                .string(
                    "Tạo đơn hàng thành công! Mã đơn hàng của bạn là: 12345")); // Kiểm tra chuỗi
    // String trả về

    // Xác thực thêm: Đảm bảo Controller đã thực sự chuyển tiếp request xuống tầng Service xử lý
    // đúng 1 lần
    verify(orderService, times(1)).createOrder(any(OrderRequest.class));
  }
}
