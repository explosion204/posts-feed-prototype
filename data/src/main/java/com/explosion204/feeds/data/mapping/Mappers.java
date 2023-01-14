package com.explosion204.feeds.data.mapping;

import com.explosion204.feeds.data.model.Comment;
import com.explosion204.feeds.data.model.Post;
import com.explosion204.feeds.data.model.PostVote;
import com.explosion204.feeds.data.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
public class Mappers {
  @Bean
  public RowMapper<User> userRowMapper() {
    return (row, metadata) -> User.builder()
            .id(row.get("id", UUID.class))
            .rating(row.get("rating", Long.class))
            .build();
  }

  @Bean
  public RowMapper<Post> postRowMapper() {
    return (row, metadata) -> Post.builder()
            .id(row.get("id", UUID.class))
            .userId(row.get("user_id", UUID.class))
            .rating(row.get("rating", Long.class))
            .createdAt(row.get("created_at", LocalDateTime.class))
            .build();
  }

  @Bean
  public RowMapper<Comment> commentRowMapper() {
    return (row, metadata) -> Comment.builder()
            .id(row.get("id", UUID.class))
            .userId(row.get("user_id", UUID.class))
            .postId(row.get("post_id", UUID.class))
            .createdAt(row.get("created_at", LocalDateTime.class))
            .build();
  }

  @Bean
  public RowMapper<PostVote> postVoteRowMapper() {
    return (row, metadata) -> PostVote.builder()
            .userId(row.get("user_id", UUID.class))
            .postId(row.get("post_id", UUID.class))
            .voteValue(PostVote.VoteValue.fromNumeric(row.get("value", Integer.class)))
            .build();
  }
}
