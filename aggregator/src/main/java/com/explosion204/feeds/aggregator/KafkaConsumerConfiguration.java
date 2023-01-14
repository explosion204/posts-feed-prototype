package com.explosion204.feeds.aggregator;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EnableKafka
@Configuration
@PropertySource("classpath:kafka-consumer.properties")
public class KafkaConsumerConfiguration {
  @Value("${kafka.consumer.bootstrap.servers}")
  private String bootstrapServers;

  @Value("${kafka.consumer.group.id}")
  private String groupId;

  @Bean
  public ConsumerFactory<UUID, Object> consumerFactory() {
    final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
    jsonDeserializer.addTrustedPackages("com.explosion204.feeds.aggregator.model");

    final Map<String, Object> properties = new HashMap<>();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(properties, new UUIDDeserializer(), jsonDeserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<UUID, Object> listenerContainerFactory(
          ConsumerFactory<UUID, Object> consumerFactory) {

    final ConcurrentKafkaListenerContainerFactory<UUID, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3);

    return factory;
  }
}
