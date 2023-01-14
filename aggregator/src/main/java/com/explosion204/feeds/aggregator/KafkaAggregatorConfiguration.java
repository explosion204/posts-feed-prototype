package com.explosion204.feeds.aggregator;

import com.explosion204.feeds.aggregator.model.PostAggregation;
import com.explosion204.feeds.aggregator.model.PostRatingAggregation;
import com.explosion204.feeds.data.kafka.events.CommentsAmountChangedEvent;
import com.explosion204.feeds.data.kafka.events.PostRatingChangedEvent;
import com.explosion204.feeds.data.kafka.events.UserRatingChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafkaStreams
@PropertySource("classpath:kafka-streams.properties")
@Slf4j
public class KafkaAggregatorConfiguration {
  private static final String TRUSTED_PACKAGE = "com.explosion204.feeds.data.kafka.events";

  @Value("${kafka.streams.bootstrap.servers}")
  private String bootstrapServers;

  @Value("${kafka.streams.application.id}")
  private String applicationId;

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
    final Map<String, Object> properties = new HashMap<>();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 500);
    properties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
            LogAndContinueExceptionHandler.class);

    return new KafkaStreamsConfiguration(properties);
  }

  @Bean
  public Topology aggregationTopology(StreamsBuilder streamsBuilder) {
    final KStream<UUID, PostRatingChangedEvent> postRatingEvents = streamsBuilder
            .stream(PostRatingChangedEvent.TOPIC, Consumed.with(Serdes.UUID(), jsonSerde(PostRatingChangedEvent.class)));

    final KTable<UUID, PostAggregation> postRatings = postRatingEvents.groupByKey()
            .aggregate(
                    PostAggregation::new,
                    (id, event, aggregation) -> {
                      if (aggregation.getPostId() == null) {
                        aggregation.setPostId(id);
                      }

                      if (aggregation.getAuthorId() == null) {
                        aggregation.setAuthorId(event.getAuthorId());
                      }

                      aggregation.setRating(aggregation.getRating() + event.getDelta());
                      return aggregation;
                    },
                    Materialized.with(Serdes.UUID(), jsonSerde(PostAggregation.class))
            );

    final KStream<UUID, UserRatingChangedEvent> userRatingEvents = streamsBuilder
            .stream(UserRatingChangedEvent.TOPIC, Consumed.with(Serdes.UUID(), jsonSerde(UserRatingChangedEvent.class)));

    final KTable<UUID, Long> userRatings = userRatingEvents.groupByKey()
            .aggregate(
                    () -> 0L,
                    (id, event, value) -> value + event.getDelta(),
                    Materialized.with(Serdes.UUID(), jsonSerde(Long.class))
            );

    final KStream<UUID, CommentsAmountChangedEvent> commentsAmountChangedEvents = streamsBuilder
            .stream(CommentsAmountChangedEvent.TOPIC, Consumed.with(Serdes.UUID(),
                    jsonSerde(CommentsAmountChangedEvent.class)));

    final KTable<UUID, Long> commentsAmounts = commentsAmountChangedEvents.groupByKey()
            .aggregate(
                    () -> 0L,
                    (id, event, value) -> value + event.getDelta(),
                    Materialized.with(Serdes.UUID(), jsonSerde(Long.class))
            );

    final KStream<UUID, PostRatingAggregation> ratingAggregation = postRatings
            .join(userRatings, PostAggregation::getAuthorId, userRatingJoiner(),
                    Materialized.with(Serdes.UUID(), jsonSerde(PostRatingAggregation.class)))
            .leftJoin(commentsAmounts, commentsAmountJoiner())
            .toStream();

    ratingAggregation.to(PostRatingAggregation.TOPIC, Produced.with(Serdes.UUID(), jsonSerde(PostRatingAggregation.class)));
    final Topology topology = streamsBuilder.build();

    log.info("Topology description: \n{}", topology.describe());

    return topology;
  }

  private ValueJoiner<PostAggregation, Long, PostRatingAggregation> userRatingJoiner() {
    return (postAggregation, userRating) -> {
      final PostRatingAggregation postRatingAggregation = new PostRatingAggregation();
      postRatingAggregation.setPostId(postAggregation.getPostId());
      postRatingAggregation.setPostRating(postAggregation.getRating());
      postRatingAggregation.setAuthorRating(userRating);
      return postRatingAggregation;
    };
  }

  private ValueJoiner<PostRatingAggregation, Long, PostRatingAggregation> commentsAmountJoiner() {
    return (postRatingAggregation, commentsAmount) -> {
      postRatingAggregation.setCommentsAmount(commentsAmount != null ? commentsAmount : 0);
      return postRatingAggregation;
    };
  }

  private <T> JsonSerde<T> jsonSerde(Class<T> targetClass) {
    final DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
    typeMapper.addTrustedPackages(TRUSTED_PACKAGE);

    final JsonSerde<T> serde = new JsonSerde<>(targetClass);
    final JsonDeserializer<T> deserializer = serde.deserializer();
    deserializer.setTypeMapper(typeMapper);
    deserializer.setUseTypeHeaders(false);

    return serde;
  }
}
