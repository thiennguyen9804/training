package com.example.order_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import com.example.order_service.client.StockServiceClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.exception.StockNotAvailableException;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

  @Mock private StockServiceClient stockServiceClient; // Giả lập Client check kho

  @Mock private OrderRepository orderRepository; // Giả lập DB Repository

  @InjectMocks private OrderService orderService; // Tiêm 2 Mock trên vào OrderService

  private OrderRequest sampleRequest;

  @BeforeEach
  public void setUp() {
    // Chuẩn bị dữ liệu mẫu: Đơn hàng gồm 2 sản phẩm khác nhau
    sampleRequest = new OrderRequest();
    sampleRequest.setCustomerId(99L);

    OrderRequest.ItemDto item1 = new OrderRequest.ItemDto();
    item1.setProductId(101L);
    item1.setQuantity(2);

    OrderRequest.ItemDto item2 = new OrderRequest.ItemDto();
    item2.setProductId(102L);
    item2.setQuantity(1);

    sampleRequest.setItems(Arrays.asList(item1, item2));
  }

  // --- KỊCH BẢN 1: THÀNH CÔNG (Tất cả sản phẩm đều đủ kho) ---
  @Test
  public void createOrder_WhenAllItemsAvailable_ShouldSaveOrderSuccessfully() {
    // 1. Arrange (Định hình hành vi)
    // Client check kho chạy mượt mà, không ném ra ngoại lệ nào (void method dùng doNothing làm mặc
    // định)
    doNothing().when(stockServiceClient).verifyProductStock(anyLong(), anyInt());

    // Giả lập lệnh lưu DB thành công và trả về chính đối tượng Order có kèm ID = 1
    Order mockSavedOrder = new Order();
    mockSavedOrder.setId(1L);
    mockSavedOrder.setCustomerId(99L);
    when(orderRepository.save(any(Order.class))).thenReturn(mockSavedOrder);

    // 2. Act (Chạy hàm kiểm thử)
    Order result = orderService.createOrder(sampleRequest);

    // 3. Assert (Kiểm tra kết quả)
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(99L, result.getCustomerId());

    // Xác thực: Client check kho phải được gọi đúng 2 lần (cho 2 sản phẩm khác nhau)
    verify(stockServiceClient, times(1)).verifyProductStock(101L, 2);
    verify(stockServiceClient, times(1)).verifyProductStock(102L, 1);

    // Kỹ thuật nâng cao: Bắt lấy Object thực tế truyền vào lệnh repository.save để kiểm tra mapping
    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository, times(1)).save(orderCaptor.capture());

    Order capturedOrder = orderCaptor.getValue();
    assertEquals(99L, capturedOrder.getCustomerId());
    assertEquals(2, capturedOrder.getItems().size()); // Đảm bảo map đủ 2 sản phẩm
    assertEquals(101L, capturedOrder.getItems().get(0).getProductId());
    assertEquals(2, capturedOrder.getItems().get(0).getQuantity());
  }

  // --- KỊCH BẢN 2: THẤT BẠI (Có sản phẩm bị hết hàng) ---
  @Test
  public void createOrder_WhenAnyItemIsNotAvailable_ShouldThrowExceptionAndNotSaveToDb() {
    // 1. Arrange (Định hình hành vi)
    // Giả lập sản phẩm 101 đủ hàng (không làm gì), nhưng sản phẩm 102 bị hết hàng (ném lỗi)
    doNothing().when(stockServiceClient).verifyProductStock(101L, 2);
    doThrow(new StockNotAvailableException("Sản phẩm ID 102 không đủ hàng."))
        .when(stockServiceClient)
        .verifyProductStock(102L, 1);

    // 2. Act & Assert (Chạy và bắt ngoại lệ)
    StockNotAvailableException exception =
        assertThrows(
            StockNotAvailableException.class,
            () -> {
              orderService.createOrder(sampleRequest);
            });

    // Kiểm tra xem câu báo lỗi có đúng chuẩn không
    assertEquals("Sản phẩm ID 102 không đủ hàng.", exception.getMessage());

    // QUAN TRỌNG: Xác thực hệ thống dừng lại ngay lập tức và TUYỆT ĐỐI KHÔNG gọi lệnh ghi vào
    // Database
    verify(orderRepository, never()).save(any(Order.class));
  }
}
