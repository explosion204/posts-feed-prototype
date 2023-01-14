package com.explosion204.feeds.data.repository;

import com.explosion204.feeds.data.mapping.RowMapper;
import com.explosion204.feeds.data.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
  private static final String INSERT_QUERY = """
          INSERT INTO comment
          VALUES (:id, :user_id, :post_id, :created_at)
          """;
  private static final String FIND_BY_ID_QUERY = """
          SELECT id, user_id, post_id, created_at
          FROM comment
          WHERE id = :id
          """;

  private static final String DELETE_BY_ID_QUERY = """
          DELETE FROM comment
          WHERE id = :id
          """;
  private final DatabaseClient databaseClient;
  private final RowMapper<Comment> commentRowMapper;

  public Mono<Comment> findById(UUID id) {
    return databaseClient.sql(FIND_BY_ID_QUERY)
            .bind("id", id)
            .map(commentRowMapper)
            .one();
  }

  public Mono<UUID> create(Comment comment) {
    final UUID id = UUID.randomUUID();
    return databaseClient.sql(INSERT_QUERY)
            .bind("id", id)
            .bind("user_id", comment.getUserId())
            .bind("post_id", comment.getPostId())
            .bind("created_at", comment.getCreatedAt())
            .fetch()
            .first()
            .thenReturn(id);
  }

  public Mono<Boolean> delete(UUID id) {
    return databaseClient.sql(DELETE_BY_ID_QUERY)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .map(rowsUpdated -> rowsUpdated > 0);
  }
}
