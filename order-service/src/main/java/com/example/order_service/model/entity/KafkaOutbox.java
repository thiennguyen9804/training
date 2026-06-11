package com.example.order_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "kafka_outboxes")
@Data
public class KafkaOutbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String topic;

  @Column(name = "message_key")
  private String messageKey;

  @Lob
  @Column(nullable = false, columnDefinition = "JSON")
  private String payload;

  @Column(nullable = false)
  private String status = "PENDING"; // PENDING, SUCCESS, FAILED
}
