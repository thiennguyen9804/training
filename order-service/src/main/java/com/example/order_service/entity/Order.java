package com.example.order_service.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  // 🚀 THÊM MỘT HÀM TIỆN ÍCH (Helper Method) để tự gán mối quan hệ 2 chiều
  public void addOrderItem(OrderItem item) {
    items.add(item);
    item.setOrder(this); // Bắt buộc phải gán dòng này để gán order_id lúc Insert
  }
}
