package com.explosion204.feeds.aggregator.model;

import lombok.Data;

import java.util.UUID;

@Data
public class PostAggregation {
  private UUID postId;
  private UUID authorId;
  private long rating;
}
