package com.example.order_service.repository;

import com.example.order_service.model.entity.KafkaOutbox;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface KafkaOutboxRepository extends CrudRepository<KafkaOutbox, Long> {
  List<KafkaOutbox> findByStatus(String status);
}
