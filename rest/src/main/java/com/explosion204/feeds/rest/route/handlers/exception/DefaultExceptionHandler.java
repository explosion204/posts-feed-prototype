package com.explosion204.feeds.rest.route.handlers.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler {
  @Override
  public boolean supports(Throwable e) {
    return true;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @Override
  public String getMessage(Throwable e) {
    log.error("Uncategorized exception: ", e);
    return "Server cannot process your request";
  }
}
