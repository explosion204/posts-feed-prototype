package com.explosion204.feeds.rest.route.handlers.exception;

import com.explosion204.feeds.rest.exception.EntityNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class EntityNotFoundExceptionHandler implements ExceptionHandler {
  @Override
  public boolean supports(Throwable e) {
    return e instanceof EntityNotFoundException;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getMessage(Throwable throwable) {
    final EntityNotFoundException exception = (EntityNotFoundException) throwable;
    return String.format("Cannot find entity %s with id = %s", exception.getName(), exception.getId());
  }
}
