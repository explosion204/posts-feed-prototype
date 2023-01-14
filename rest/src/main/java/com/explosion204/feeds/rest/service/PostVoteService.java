package com.explosion204.feeds.rest.service;

import com.explosion204.feeds.data.kafka.events.PostRatingChangedEvent;
import com.explosion204.feeds.data.kafka.events.UserRatingChangedEvent;
import com.explosion204.feeds.data.model.Post;
import com.explosion204.feeds.data.model.PostVote;
import com.explosion204.feeds.data.repository.PostVoteRepository;
import com.explosion204.feeds.rest.exception.EntityAlreadyExistsException;
import com.explosion204.feeds.rest.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostVoteService {
  private static final String ENTITY_NAME = PostVote.class.getSimpleName();

  private final PostVoteRepository voteRepository;
  private final UserService userService;
  private final PostService postService;

  private final KafkaService<UUID, Object> kafkaService;

  public Mono<PostVote> findById(UUID userId, UUID postId) {
    return voteRepository.findById(userId, postId)
            .switchIfEmpty(Mono.error(new EntityNotFoundException(Pair.of(userId, postId), ENTITY_NAME)));
  }

  public Mono<Pair<UUID, UUID>> create(PostVote postVote) {
    final UUID userId = postVote.getUserId();
    final UUID postId = postVote.getPostId();
    final PostVote.VoteValue value = postVote.getVoteValue();

    return voteRepository.findById(userId, postId)
            .flatMap(item -> Mono.error(new EntityAlreadyExistsException(Pair.of(userId, postId), ENTITY_NAME)))
            .switchIfEmpty(voteRepository.create(postVote))
            .then(updateRatings(postId, value.getNumeric()))
            .thenReturn(Pair.of(userId, postId));
  }

  @Transactional
  public Mono<Pair<UUID, UUID>> update(UUID userId, UUID postId, PostVote postVote) {
    return voteRepository.findById(userId, postId)
            // interrupt all next operations if vote value is not changed
            .filter(existingVote -> existingVote.getVoteValue() != postVote.getVoteValue())
            .doOnNext(existingVote -> existingVote.setVoteValue(postVote.getVoteValue()))
            .flatMap(voteRepository::update)
            .flatMap(success -> success
                    ? Mono.just(postVote.getVoteValue())
                    : Mono.error(new EntityNotFoundException(Pair.of(userId, postId), ENTITY_NAME))
            )
            .flatMap(voteValue -> updateRatings(postId, 2 * voteValue.getNumeric()))
            .thenReturn(Pair.of(userId, postId));
  }

  @Transactional
  public Mono<Void> delete(UUID userId, UUID postId) {
    return findById(userId, postId)
            .flatMap(postVote -> updateRatings(postId, -postVote.getVoteValue().getNumeric()))
            .then(voteRepository.delete(userId, postId))
            .then();
  }

  private Mono<Void> updateRatings(UUID postId, int numericDelta) {
    final Mono<Post> targetPost = postService.findById(postId).cache();

    // update post rating
    return targetPost.doOnNext(post -> post.setRating(post.getRating() + numericDelta))
            .flatMap(post -> postService.update(post.getId(), post))
            // then update author rating
            .then(targetPost)
            .flatMap(post -> userService.findById(post.getUserId()))
            .doOnNext(user -> user.setRating(user.getRating() + numericDelta))
            .flatMap(user -> userService.update(user.getId(), user))
            // send event that user rating has changed
            .map(userId -> new UserRatingChangedEvent(userId, numericDelta))
            .flatMap(event -> kafkaService.publish(UserRatingChangedEvent.TOPIC, event.getUserId(), event))
            // send event that post rating has changed
            .then(targetPost)
            .map(post -> new PostRatingChangedEvent(post.getId(), post.getUserId(), numericDelta))
            .flatMap(event -> kafkaService.publish(PostRatingChangedEvent.TOPIC, event.getPostId(), event));
  }
}
