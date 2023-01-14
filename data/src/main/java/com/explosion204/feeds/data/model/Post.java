package com.explosion204.feeds.data.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Post {
  private UUID id;
  private UUID userId;
  private Long rating;
  private LocalDateTime createdAt;
}
