package com.explosion204.feeds.rest.route.handlers;

import com.explosion204.feeds.data.model.User;
import com.explosion204.feeds.rest.route.RequestBodyExtractor;
import com.explosion204.feeds.rest.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserHandler {
  private final RequestBodyExtractor bodyExtractor;
  private final UserService userService;

  public Mono<ServerResponse> findById(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return userService.findById(id)
            .map(this::fromUser)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    return userService.create()
            .flatMap(userService::findById)
            .map(this::fromUser)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return bodyExtractor.extract(request, UserRestModel.class)
            .map(this::toUser)
            .flatMap(user -> userService.update(id, user))
            .flatMap(userService::findById)
            .map(this::fromUser)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> delete(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return userService.delete(id)
            .then(ServerResponse.noContent().build());
  }

  private User toUser(UserRestModel restModel) {
    return User.builder()
            .id(restModel.id)
            .rating(restModel.rating)
            .build();
  }

  private UserRestModel fromUser(User user) {
    final UserRestModel restModel = new UserRestModel();
    restModel.id = user.getId();
    restModel.rating = user.getRating();
    return restModel;
  }

  @Data
  public static class UserRestModel {
    @Null
    private UUID id;
    @NotNull
    private Long rating;
  }
}
