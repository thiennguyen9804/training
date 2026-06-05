package com.example.shipping_service.config;

import com.example.shipping_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
  private final ShippingService service;

  @Bean
  public ThreadPoolTaskExecutor pipelineExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(5);       // Số lượng thread chạy thường trực
      executor.setMaxPoolSize(10);       // Số lượng thread tối đa khi quá tải
      executor.setQueueCapacity(20);     // Hàng đợi chứa task chờ xử lý
      executor.initialize();
      return executor;
  }

  @Bean
  public ExecutorChannel primaryChannel(ThreadPoolTaskExecutor pipelineExecutor) {
    return new ExecutorChannel(pipelineExecutor);
  }

  @Bean
  public DirectChannel wireTapChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow wireTapFlow() {
    return IntegrationFlow.from(primaryChannel())
        .wireTap(wireTapChannel())
        .handle(service, "saveShipping")
        .get();
  }

  @Bean
  public IntegrationFlow loggingWireTapFlow(DirectChannel wireTapChannel) {
    return IntegrationFlow.from(wireTapChannel)
        .handle(
            message -> {
              // Nơi bạn xử lý bản sao tin nhắn (ví dụ: in log)
              System.out.println("WireTap nhận được bản sao tin nhắn: " + message.getPayload());
            })
        .get();
  }
}
