package com.explosion204.feeds.rest.route.handlers;

import com.explosion204.feeds.data.model.Post;
import com.explosion204.feeds.rest.route.RequestBodyExtractor;
import com.explosion204.feeds.rest.route.ValidationGroups;
import com.explosion204.feeds.rest.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostHandler {
  private final RequestBodyExtractor bodyExtractor;
  private final PostService postService;

  public Mono<ServerResponse> findById(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return postService.findById(id)
            .map(this::fromPost)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    return bodyExtractor.extract(request, PostRestModel.class, ValidationGroups.CreateGroup.class)
            .map(this::toPost)
            .flatMap(postService::create)
            .flatMap(postService::findById)
            .map(this::fromPost)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return bodyExtractor.extract(request, PostRestModel.class, ValidationGroups.UpdateGroup.class)
            .map(this::toPost)
            .flatMap(post -> postService.update(id, post))
            .flatMap(postService::findById)
            .map(this::fromPost)
            .flatMap(modle -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(modle)));
  }

  public Mono<ServerResponse> delete(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return postService.delete(id)
            .then(ServerResponse.noContent().build());
  }

  public Post toPost(PostRestModel restModel) {
    return Post.builder()
            .id(restModel.id)
            .userId(restModel.userId)
            .rating(restModel.rating)
            .createdAt(restModel.createdAt)
            .build();
  }

  public PostRestModel fromPost(Post post) {
    final PostRestModel restModel = new PostRestModel();
    restModel.id = post.getId();
    restModel.userId = post.getUserId();
    restModel.rating = post.getRating();
    restModel.createdAt = post.getCreatedAt();
    return restModel;
  }

  @Data
  public static class PostRestModel {
    @Null(groups = ValidationGroups.All.class)
    private UUID id;

    @NotNull(groups = ValidationGroups.CreateGroup.class)
    @Null(groups = ValidationGroups.UpdateGroup.class)
    private UUID userId;

    @NotNull(groups = ValidationGroups.UpdateGroup.class)
    @Null(groups = ValidationGroups.CreateGroup.class)
    private Long rating;

    @Null(groups = ValidationGroups.All.class)
    private LocalDateTime createdAt;
  }
}
