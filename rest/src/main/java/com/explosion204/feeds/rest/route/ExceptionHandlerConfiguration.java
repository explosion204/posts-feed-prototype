package com.explosion204.feeds.rest.route;

import com.explosion204.feeds.rest.model.ExceptionEntity;
import com.explosion204.feeds.rest.route.handlers.exception.ExceptionHandler;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Order(-2)
public class ExceptionHandlerConfiguration extends AbstractErrorWebExceptionHandler {
  private final List<ExceptionHandler> handlers;

  public ExceptionHandlerConfiguration(ErrorAttributes errorAttributes,
                                       ApplicationContext applicationContext,
                                       ServerCodecConfigurer codecConfigurer,
                                       List<ExceptionHandler> handlers) {

    super(errorAttributes, new WebProperties.Resources(), applicationContext);
    this.setMessageWriters(codecConfigurer.getWriters());
    this.handlers = handlers;
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::handle);
  }

  private Mono<ServerResponse> handle(ServerRequest request) {
    final Throwable e = getError(request);
    final ExceptionHandler exceptionHandler = handlers.stream()
            .filter(handler -> handler.supports(e))
            .findFirst()
            .orElseThrow();

    final HttpStatus status = exceptionHandler.getStatus();
    final String message = exceptionHandler.getMessage(e);

    return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(ExceptionEntity.builder()
                    .message(message)
                    .build()));
  }
}
