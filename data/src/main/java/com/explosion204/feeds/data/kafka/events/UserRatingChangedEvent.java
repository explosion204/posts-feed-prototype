package com.explosion204.feeds.data.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingChangedEvent {
  public static final String TOPIC = "user-rating-changed-events";

  private UUID userId;
  private int delta;
}
