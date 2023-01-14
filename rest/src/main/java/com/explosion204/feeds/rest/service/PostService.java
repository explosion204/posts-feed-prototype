package com.explosion204.feeds.rest.service;

import com.explosion204.feeds.data.model.Post;
import com.explosion204.feeds.data.repository.PostRepository;
import com.explosion204.feeds.rest.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
  private static final String ENTITY_NAME = Post.class.getSimpleName();
  private final PostRepository postRepository;

  public Mono<Post> findById(UUID id) {
    return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException(id, ENTITY_NAME)));
  }

  public Mono<UUID> create(Post post) {
    post.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));

    return postRepository.create(post);
  }

  public Mono<UUID> update(UUID id, Post post) {
    post.setId(id);

    return postRepository.update(post)
            .flatMap(success -> success ? Mono.just(id) : Mono.error(new EntityNotFoundException(id, ENTITY_NAME)));
  }

  public Mono<Void> delete(UUID id) {
    return postRepository.delete(id)
            .flatMap(success -> success ? Mono.empty() : Mono.error(new EntityNotFoundException(id, ENTITY_NAME)))
            .then();
  }
}
