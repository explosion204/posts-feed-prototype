package com.explosion204.feeds.rest.route;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationGroups {
  public interface CreateGroup { }
  public interface UpdateGroup { }

  @GroupSequence({ CreateGroup.class, UpdateGroup.class })
  public interface All { }
}
