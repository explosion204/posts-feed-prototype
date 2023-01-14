package com.explosion204.feeds.rest.route.handlers.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionHandler {
  boolean supports(Throwable e);
  HttpStatus getStatus();
  String getMessage(Throwable e);
}
