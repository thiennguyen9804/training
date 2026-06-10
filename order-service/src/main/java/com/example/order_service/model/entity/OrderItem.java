package com.example.order_service.model.entity;

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

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  @JsonIgnore
  private Order order;

  public OrderItem(Long productId, int quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public OrderItem() {}
}
