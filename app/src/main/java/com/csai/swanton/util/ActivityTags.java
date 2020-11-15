package com.csai.swanton.util;

import lombok.Getter;

public final class ActivityTags {

  public enum Tags {
    MAIN_ACTIVITY("MainActivity");

    @Getter
    private final String tag;

    Tags(final String tag) {
      this.tag = tag;
    }
  }
}
