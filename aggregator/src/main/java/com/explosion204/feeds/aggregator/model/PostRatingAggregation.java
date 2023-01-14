package com.explosion204.feeds.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRatingAggregation {
  public static final String TOPIC = "post-rating-aggregation";

  private UUID postId;
  private long postRating;
  private long authorRating;
  private long commentsAmount;
}
