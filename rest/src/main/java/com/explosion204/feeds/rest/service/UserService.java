package com.explosion204.feeds.rest.service;

import com.explosion204.feeds.data.model.User;
import com.explosion204.feeds.data.repository.UserRepository;
import com.explosion204.feeds.rest.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
  private static final String ENTITY_NAME = User.class.getSimpleName();
  private final UserRepository userRepository;

  public Mono<User> findById(UUID id) {
    return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException(id, ENTITY_NAME)));
  }

  public Mono<UUID> create() {
    return userRepository.create();
  }

  public Mono<UUID> update(UUID id, User user) {
    user.setId(id);

    return userRepository.update(user)
            .flatMap(success -> success ? Mono.just(id) : Mono.error(new EntityNotFoundException(id, ENTITY_NAME)));
  }

  public Mono<Void> delete(UUID id) {
    return userRepository.delete(id)
            .flatMap(success -> success ? Mono.empty() : Mono.error(new EntityNotFoundException(id, ENTITY_NAME)))
            .then();
  }
}
