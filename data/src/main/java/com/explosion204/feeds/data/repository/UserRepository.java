package com.explosion204.feeds.data.repository;

import com.explosion204.feeds.data.mapping.RowMapper;
import com.explosion204.feeds.data.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository {
  private static final String INSERT_QUERY = """
          INSERT INTO app_user (id)
          VALUES (:id)
          """;

  private static final String UPDATE_QUERY = """
          UPDATE app_user
          SET rating = :rating
          WHERE id = :id
          """;

  private static final String FIND_BY_ID_QUERY = """
          SELECT id, rating
          FROM app_user
          WHERE id = :id
          """;

  private static final String DELETE_BY_ID_QUERY = """
          DELETE FROM app_user
          WHERE id = :id
          """;
  private final DatabaseClient databaseClient;
  private final RowMapper<User> userRowMapper;

  public Mono<User> findById(UUID id) {
    return databaseClient.sql(FIND_BY_ID_QUERY)
            .bind("id", id)
            .map(userRowMapper)
            .one();
  }

  public Mono<UUID> create() {
    final UUID id = UUID.randomUUID();
    return databaseClient.sql(INSERT_QUERY)
            .bind("id", id)
            .fetch()
            .first()
            .thenReturn(id);
  }

  public Mono<Boolean> update(User user) {
    return databaseClient.sql(UPDATE_QUERY)
            .bind("id", user.getId())
            .bind("rating", user.getRating())
            .fetch()
            .rowsUpdated()
            .map(rowsUpdated -> rowsUpdated > 0);
  }

  public Mono<Boolean> delete(UUID id) {
    return databaseClient.sql(DELETE_BY_ID_QUERY)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .map(rowsUpdated -> rowsUpdated > 0);
  }
}
