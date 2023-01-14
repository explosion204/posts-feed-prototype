package com.explosion204.feeds.rest.service;

import com.explosion204.feeds.data.kafka.events.CommentsAmountChangedEvent;
import com.explosion204.feeds.data.model.Comment;
import com.explosion204.feeds.data.repository.CommentRepository;
import com.explosion204.feeds.rest.exception.EntityNotFoundException;
import com.explosion204.feeds.rest.service.kafka.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
  private static final String ENTITY_NAME = Comment.class.getSimpleName();

  private final CommentRepository commentRepository;
  private final KafkaService<UUID, Object> kafkaService;

  public Mono<Comment> findById(UUID id) {
    return commentRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException(id, ENTITY_NAME)));
  }

  public Mono<UUID> create(Comment comment) {
    comment.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));
    final Mono<UUID> createdId = commentRepository.create(comment).cache();

    return createdId.map(commentId -> new CommentsAmountChangedEvent(comment.getPostId(), 1))
            .flatMap(event -> kafkaService.publish(CommentsAmountChangedEvent.TOPIC, event.getPostId(), event))
            .then(createdId);
  }

  public Mono<Void> delete(UUID id) {
    final Mono<Comment> commentToDelete = findById(id).cache();

    return commentToDelete.flatMap(comment -> commentRepository.delete(comment.getId()))
            .flatMap(success -> success ? Mono.just(id) : Mono.error(new EntityNotFoundException(id, ENTITY_NAME)))
            .then(commentToDelete)
            .map(comment -> new CommentsAmountChangedEvent(comment.getPostId(), -1))
            .flatMap(event -> kafkaService.publish(CommentsAmountChangedEvent.TOPIC, event.getPostId(), event));
  }
}
