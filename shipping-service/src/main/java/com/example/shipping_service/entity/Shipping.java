package com.example.shipping_service.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚀 Lưu ID dạng số thuần túy (Không dùng @OneToOne vì bảng Order nằm ở DB khác)
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 100)
    private String trackingNumber;

    @Column(name = "shipping_status", nullable = false, length = 50)
    private String shippingStatus;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}