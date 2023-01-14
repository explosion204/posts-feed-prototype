CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    rating BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE post (
  id UUID PRIMARY KEY,
  user_id UUID,
  rating BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP,

  CONSTRAINT post_to_user_fk FOREIGN KEY (user_id) REFERENCES app_user (id),
  CONSTRAINT post_created_at_range CHECK (created_at >= TIMESTAMP '1970-01-01 00:00:00' AND created_at <= TIMESTAMP '2222-04-20 03:14:07')
);

CREATE TABLE post_vote (
    user_id UUID,
    post_id UUID,
    voteValue SMALLINT,

    PRIMARY KEY (user_id, post_id),
    CONSTRAINT post_vote_to_user_fk FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT post_vote_to_post_fk FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT post_vote_value_range CHECK (voteValue >= -1 AND voteValue <= 1)
);

CREATE TABLE comment (
    id UUID PRIMARY KEY,
    user_id UUID,
    post_id UUID,
    created_at TIMESTAMP,

    CONSTRAINT comment_to_user_fk FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT comment_to_post_fk FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT comment_created_at_range CHECK (created_at >= TIMESTAMP '1970-01-01 00:00:00' AND created_at <= TIMESTAMP '2222-04-20 03:14:07')
);