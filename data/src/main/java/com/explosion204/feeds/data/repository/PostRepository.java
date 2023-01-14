package com.explosion204.feeds.data.repository;

import com.explosion204.feeds.data.mapping.RowMapper;
import com.explosion204.feeds.data.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostRepository {
  private static final String FIND_BY_ID_QUERY = """
          SELECT id, user_id, rating, created_at
          FROM post
          WHERE id = :id
          """;

  private static final String INSERT_QUERY = """
          INSERT INTO post (id, user_id, created_at)
          VALUES (:id, :user_id, :created_at)
          """;

  private static final String UPDATE_QUERY = """
          UPDATE post
          SET rating = :rating
          WHERE id = :id
          """;

  private static final String DELETE_BY_ID_QUERY = """
          DELETE FROM post
          WHERE id = :id
          """;

  private final DatabaseClient databaseClient;
  private final RowMapper<Post> postRowMapper;

  public Mono<Post> findById(UUID id) {
    return databaseClient.sql(FIND_BY_ID_QUERY)
            .bind("id", id)
            .map(postRowMapper)
            .one();
  }

  public Mono<UUID> create(Post post) {
    final UUID id = UUID.randomUUID();
    return databaseClient.sql(INSERT_QUERY)
            .bind("id", id)
            .bind("user_id", post.getUserId())
            .bind("created_at", post.getCreatedAt())
            .fetch()
            .first()
            .thenReturn(id);
  }

  public Mono<Boolean> update(Post post) {
    return databaseClient.sql(UPDATE_QUERY)
            .bind("id", post.getId())
            .bind("rating", post.getRating())
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
