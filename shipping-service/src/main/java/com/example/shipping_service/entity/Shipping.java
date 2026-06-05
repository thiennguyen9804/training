package com.example.shipping_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "shippings")
@Data
public class Shipping {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_id", nullable = false, unique = true)
  private Long orderId;

  @Column(name = "tracking_number", nullable = false, unique = true, length = 36)
  private String trackingNumber;

  @Column(name = "shipping_status", nullable = false)
  private String shippingStatus;

  @Column(name = "address", nullable = false, columnDefinition = "TEXT")
  private String address;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
