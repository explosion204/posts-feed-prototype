package com.explosion204.feeds.rest.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityAlreadyExistsException extends RuntimeException {
  private final Object id;
  private final String entityName;
}
