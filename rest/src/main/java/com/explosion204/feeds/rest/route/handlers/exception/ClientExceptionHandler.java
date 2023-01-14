package com.explosion204.feeds.rest.route.handlers.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;

@Component
@Order(2)
@Slf4j
public class ClientExceptionHandler implements ExceptionHandler {
  @Override
  public boolean supports(Throwable e) {
    return e instanceof IllegalArgumentException ||
            e instanceof ValidationException;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.BAD_REQUEST;
  }

  @Override
  public String getMessage(Throwable e) {
    log.error("Bad request: ", e);
    return "Something wrong with your inputs";
  }
}
