package com.explosion204.feeds.rest.route.handlers;

import com.explosion204.feeds.data.model.PostVote;
import com.explosion204.feeds.rest.route.RequestBodyExtractor;
import com.explosion204.feeds.rest.route.ValidationGroups;
import com.explosion204.feeds.rest.service.PostVoteService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostVoteHandler {
  private final RequestBodyExtractor bodyExtractor;
  private final PostVoteService voteService;

  public Mono<ServerResponse> findById(ServerRequest request) {
    final UUID userId = UUID.fromString(request.pathVariable("user_id"));
    final UUID postId = UUID.fromString(request.pathVariable("post_id"));
    return voteService.findById(userId, postId)
            .map(this::fromPostVote)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> create(ServerRequest request) {
    return bodyExtractor.extract(request, PostVoteRestModel.class, ValidationGroups.CreateGroup.class)
            .map(this::toPostVote)
            .flatMap(voteService::create)
            .flatMap(idPair -> voteService.findById(idPair.getLeft(), idPair.getRight()))
            .map(this::fromPostVote)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    final UUID userId = UUID.fromString(request.pathVariable("user_id"));
    final UUID postId = UUID.fromString(request.pathVariable("post_id"));
    return bodyExtractor.extract(request, PostVoteRestModel.class, ValidationGroups.UpdateGroup.class)
            .map(this::toPostVote)
            .flatMap(postVote -> voteService.update(userId, postId, postVote))
            .flatMap(idPair -> voteService.findById(idPair.getLeft(), idPair.getRight()))
            .map(this::fromPostVote)
            .flatMap(model -> ServerResponse.ok()
                    .body(BodyInserters.fromValue(model)));
  }

  public Mono<ServerResponse> delete(ServerRequest request) {
    final UUID userId = UUID.fromString(request.pathVariable("user_id"));
    final UUID postId = UUID.fromString(request.pathVariable("post_id"));
    return voteService.delete(userId, postId)
            .then(ServerResponse.noContent().build());
  }

  public PostVote toPostVote(PostVoteRestModel restModel) {
    final int voteValue = Integer.parseInt(restModel.value);
    return PostVote.builder()
            .userId(restModel.userId)
            .postId(restModel.postId)
            .voteValue(PostVote.VoteValue.fromNumeric(voteValue))
            .build();
  }

  public PostVoteRestModel fromPostVote(PostVote postVote) {
    final PostVoteRestModel restModel = new PostVoteRestModel();
    restModel.userId = postVote.getUserId();
    restModel.postId = postVote.getPostId();
    restModel.value = String.valueOf(postVote.getVoteValue().getNumeric());
    return restModel;
  }

  @Data
  public static class PostVoteRestModel {
    @NotNull(groups = ValidationGroups.CreateGroup.class)
    @Null(groups = ValidationGroups.UpdateGroup.class)
    private UUID userId;

    @NotNull(groups = ValidationGroups.CreateGroup.class)
    @Null(groups = ValidationGroups.UpdateGroup.class)
    private UUID postId;

    @NotNull(groups = ValidationGroups.All.class)
    @Pattern(regexp = "-1|1")
    private String value;
  }
}
