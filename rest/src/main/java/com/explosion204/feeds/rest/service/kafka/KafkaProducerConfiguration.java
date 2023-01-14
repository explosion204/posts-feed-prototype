package com.explosion204.feeds.rest.service.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@PropertySource("classpath:kafka-producer.properties")
public class KafkaProducerConfiguration {
  @Value("${kafka.producer.bootstrap.servers}")
  private String bootstrapServers;

  @Bean
  public KafkaSender<UUID, Object> kafkaSender() {
    final Map<String, Object> properties = new HashMap<>();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    final SenderOptions<UUID, Object> senderOptions = SenderOptions.create(properties);
    return KafkaSender.create(senderOptions);
  }
}
