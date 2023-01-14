package com.explosion204.feeds.rest.route;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestBodyExtractor {
  private final Validator validator;

  public <T> Mono<T> extract(ServerRequest request, Class<T> bodyClass, Class<?> ... validationGroups) {
    return request.bodyToMono(bodyClass)
            .defaultIfEmpty(createEmptyObject(bodyClass))
            .flatMap(item -> validate(item, validationGroups));
  }

  private <T> Mono<T> validate(T item, Class<?> ... validationGroups) {
    final Set<ConstraintViolation<T>> violations = validator.validate(item, validationGroups);

    if (violations == null || violations.isEmpty()) {
      return Mono.just(item);
    }

    return Mono.error(new ConstraintViolationException(violations));
  }

  @SneakyThrows
  private <T> T createEmptyObject(Class<T> clazz) {
    return clazz.getDeclaredConstructor().newInstance();
  }
}
