package com.explosion204.feeds.data.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRatingChangedEvent {
  public static final String TOPIC = "post-rating-changed-events";

  private UUID postId;
  private UUID authorId;
  private int delta;
}
