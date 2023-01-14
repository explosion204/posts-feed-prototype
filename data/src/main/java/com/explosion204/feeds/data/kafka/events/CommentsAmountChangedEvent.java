package com.explosion204.feeds.data.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentsAmountChangedEvent {
  public static final String TOPIC = "comments-amount-changed-events";

  private UUID postId;
  private int delta;
}
