package com.example.shipping_service.config;

import com.example.shipping_service.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
  private final ShippingService service;
  private final Logger logger = LogManager.getLogger();

  @Bean
  public ThreadPoolTaskExecutor pipelineExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(20);
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
    return IntegrationFlow.from(primaryChannel(pipelineExecutor()))
        .wireTap(wireTapChannel())
        .handle(service, "saveShipping")
        .get();
  }

  @Bean
  public IntegrationFlow loggingWireTapFlow(DirectChannel wireTapChannel) {
    return IntegrationFlow.from(wireTapChannel)
        .handle(
            message -> logger.info("WireTap received: {}", message.getPayload()))
        .get();
  }
}
