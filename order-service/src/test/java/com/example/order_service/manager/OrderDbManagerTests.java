package com.example.order_service.manager;

import com.example.order_service.dto.OrderRequest;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderDbManagerTests {
    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderDbManager manager;

    @Test
    void testSaveOrderTx_Success() {
        OrderRequest.ItemDto item1 = new OrderRequest.ItemDto(101L, 2);
        OrderRequest.ItemDto item2 = new OrderRequest.ItemDto(102L, 5);
        OrderRequest request = new OrderRequest(1L, List.of(item1, item2));
        Order mockSavedOrder = new Order();
        mockSavedOrder.setId(999L); // Giả lập DB tự tăng ID sau khi lưu thành công
        when(repository.save(any(Order.class))).thenReturn(mockSavedOrder);
        Order result = manager.saveOrderTx(request);
        assertNotNull(result);
        assertEquals(999L, result.getId());
    }
}
