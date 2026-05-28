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

  @OneToMany(
          mappedBy = "order",
          cascade = CascadeType.ALL,
          orphanRemoval = true,
          fetch = FetchType.LAZY
  )
  private List<OrderItem> items = new ArrayList<>();

  public void addOrderItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }
}
