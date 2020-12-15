package com.csai.swanton.util;

import android.util.Log;
import com.csai.swanton.util.ActivityTags.Tags;
import java.io.File;

/**
 * Class that holds the logic for setting up the local swanton directory.
 */
public final class DirectoryHandler {

  /**
   * Creates a local directory if it doesn't exist already.
   * @param dir The directory to create.
   */
  public static void createLocalDirectory(final String dir) {
    try {
      final File newDir = new File(dir);
      if (!newDir.exists()) {
        Log.i(Tags.MAIN_ACTIVITY.getTag(), String.format("Creating directory: %s", dir));
        newDir.mkdirs();
      }
    } catch (final Exception e) {
      Log.e(
          Tags.MAIN_ACTIVITY.getTag(),
          String.format("Could not find or create folder %s: %s", dir, e));
      throw new RuntimeException(e);
    }
  }

  private DirectoryHandler() {}
}
