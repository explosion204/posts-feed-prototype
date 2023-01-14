package com.explosion204.feeds.data.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.UUID;

@Data
@Builder
public class PostVote {
  private UUID userId;
  private UUID postId;
  private VoteValue voteValue;

  @Getter
  @RequiredArgsConstructor
  public enum VoteValue {
    UPVOTE(1), DOWNVOTE(-1);

    private final int numeric;

    public static VoteValue fromNumeric(int numeric) {
      return Arrays.stream(VoteValue.values())
              .filter(voteValue -> voteValue.numeric == numeric)
              .findFirst()
              .orElseThrow();
    }
  }
}
