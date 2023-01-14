package com.explosion204.feeds.aggregator.consumer;

import com.explosion204.feeds.aggregator.model.PostRatingAggregation;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class PostRatingAggregationListener {

  @KafkaListener(topics = PostRatingAggregation.TOPIC, containerFactory = "listenerContainerFactory")
  public void handle(ConsumerRecord<UUID, PostRatingAggregation> message) {
    final PostRatingAggregation aggregation = message.value();
    log.info("Rank evaluation will be performed for post with id = {}", message.key());
    log.info("Post ID: {}\npost rating: {}\nauthor rating: {}\ncomments amount: {}", aggregation.getPostId(),
            aggregation.getPostRating(), aggregation.getAuthorRating(), aggregation.getCommentsAmount());
  }
}
