package com.explosion204.feeds.rest.route.handlers;

import com.explosion204.feeds.data.model.Comment;
import com.explosion204.feeds.rest.route.RequestBodyExtractor;
import com.explosion204.feeds.rest.service.CommentService;
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
public class CommentHandler {
  private final RequestBodyExtractor bodyExtractor;
  private final CommentService commentService;

  public Mono<ServerResponse> findById(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return commentService.findById(id)
            .map(this::fromComment)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    return bodyExtractor.extract(request, CommentRestModel.class)
            .map(this::toComment)
            .flatMap(commentService::create)
            .flatMap(commentService::findById)
            .map(this::fromComment)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> delete(ServerRequest request) {
    final UUID id = UUID.fromString(request.pathVariable("id"));
    return commentService.delete(id)
            .then(ServerResponse.noContent().build());
  }

  private Comment toComment(CommentRestModel restModel) {
    return Comment.builder()
            .id(restModel.id)
            .userId(restModel.userId)
            .postId(restModel.postId)
            .createdAt(restModel.createdAt)
            .build();
  }

  private CommentRestModel fromComment(Comment comment) {
    final CommentRestModel restModel = new CommentRestModel();
    restModel.id = comment.getId();
    restModel.userId = comment.getUserId();
    restModel.postId = comment.getPostId();
    restModel.createdAt = comment.getCreatedAt();
    return restModel;
  }

  @Data
  public static class CommentRestModel {
    @Null
    private UUID id;

    @NotNull
    private UUID userId;

    @NotNull
    private UUID postId;

    @Null
    private LocalDateTime createdAt;
  }
}
