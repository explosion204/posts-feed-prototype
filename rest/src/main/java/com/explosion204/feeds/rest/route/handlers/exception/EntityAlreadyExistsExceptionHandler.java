package com.explosion204.feeds.rest.route.handlers.exception;

import com.explosion204.feeds.rest.exception.EntityAlreadyExistsException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class EntityAlreadyExistsExceptionHandler implements ExceptionHandler {
  @Override
  public boolean supports(Throwable e) {
    return e instanceof EntityAlreadyExistsException;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.CONFLICT;
  }

  @Override
  public String getMessage(Throwable throwable) {
    final EntityAlreadyExistsException exception = (EntityAlreadyExistsException) throwable;
    return String.format("Entity %s with id = %s already exists", exception.getEntityName(), exception.getId());
  }
}
