package com.example.order_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(nullable = false)
  private int quantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false) // Trùng tên cột khóa ngoại dưới DB
  @JsonIgnore // Tránh bị lỗi lặp vô hạn khi log hoặc chuyển JSON
  private Order order;

  // Getter, Setter, Constructor không tham số
  public OrderItem() {}

  public OrderItem(Long productId, int quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }
}
