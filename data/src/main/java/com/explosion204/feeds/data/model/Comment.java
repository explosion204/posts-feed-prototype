package com.explosion204.feeds.data.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Comment {
  private UUID id;
  private UUID userId;
  private UUID postId;
  private LocalDateTime createdAt;
}
