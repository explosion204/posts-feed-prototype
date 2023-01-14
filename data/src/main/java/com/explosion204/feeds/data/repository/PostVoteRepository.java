package com.explosion204.feeds.data.repository;

import com.explosion204.feeds.data.mapping.RowMapper;
import com.explosion204.feeds.data.model.PostVote;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostVoteRepository {
  private static final String INSERT_QUERY = """
          INSERT INTO post_vote
          VALUES (:user_id, :post_id, :voteValue)
          """;

  private static final String UPDATE_QUERY = """
          UPDATE post_vote
          SET value = :voteValue
          WHERE user_id = :user_id AND post_id = :post_id
          """;

  private static final String FIND_BY_ID_QUERY = """
          SELECT user_id, post_id, value
          FROM post_vote
          WHERE user_id = :user_id AND post_id = :post_id
          """;
  private static final String DELETE_BY_ID_QUERY = """
          DELETE FROM post_vote
          WHERE user_id = :user_id AND post_id = :post_id
          """;
  private final DatabaseClient databaseClient;
  private final RowMapper<PostVote> postVoteRowMapper;

  public Mono<PostVote> findById(UUID userId, UUID postId) {
    return databaseClient.sql(FIND_BY_ID_QUERY)
            .bind("user_id", userId)
            .bind("post_id", postId)
            .map(postVoteRowMapper)
            .one();
  }

  public Mono<UUID> create(PostVote vote) {
    final UUID id = UUID.randomUUID();
    return databaseClient.sql(INSERT_QUERY)
            .bind("user_id", vote.getUserId())
            .bind("post_id", vote.getPostId())
            .bind("voteValue", vote.getVoteValue().getNumeric())
            .fetch()
            .first()
            .thenReturn(id);
  }

  public Mono<Boolean> update(PostVote vote) {
    return databaseClient.sql(UPDATE_QUERY)
            .bind("user_id", vote.getUserId())
            .bind("post_id", vote.getPostId())
            .bind("voteValue", vote.getVoteValue().getNumeric())
            .fetch()
            .rowsUpdated()
            .map(rowsUpdated -> rowsUpdated > 0);
  }

  public Mono<Boolean> delete(UUID userId, UUID postId) {
    return databaseClient.sql(DELETE_BY_ID_QUERY)
            .bind("user_id", userId)
            .bind("post_id", postId)
            .fetch()
            .rowsUpdated()
            .map(rowsUpdated -> rowsUpdated > 0);
  }
}
