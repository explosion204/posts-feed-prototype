package com.explosion204.feeds.rest.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class KafkaService<K, V> {
  private final KafkaSender<K, V> kafkaSender;

  public Mono<Void> publish(String topic, K key, V value) {
    final SenderRecord<K, V, Object> record = SenderRecord.create(topic, null, null, key, value, null);
    return kafkaSender.send(Mono.just(record))
            .then();
  }
}
