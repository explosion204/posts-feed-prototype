package com.explosion204.feeds.rest.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionEntity {
  private String message;
}
