package com.explosion204.feeds.data.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table(name = "app_user")
public class User {
  private UUID id;
  private Long rating;
}
