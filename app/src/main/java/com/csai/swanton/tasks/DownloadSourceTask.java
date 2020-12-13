package com.csai.swanton.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.csai.swanton.util.ActivityTags.Tags;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;

/**
 * Asynchronous task for downloading the swanton app source code.
 */
@AllArgsConstructor
public class DownloadSourceTask extends AsyncTask<Void, Void, Optional<String>> {
  private static final String SOURCE_URL = "https://github.com/calpoly-csai/swanton/archive/master.zip";
  private static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.US);

  public static final String LOCAL_SWANTON_ZIP_DIR = "./sdcard/Swanton/zip/";

  @Nonnull
  private final WeakReference<View> view;

  @Override
  protected void onPreExecute() {
    Snackbar.make(
        this.view.get(),
        "Downloading .zip from github.com/calpoly-csai/swanton ...",
        Snackbar.LENGTH_SHORT).show();
    Log.i(
        Tags.MAIN_ACTIVITY.getTag(),
        "Downloading .zip from github.com/calpoly-csai/swanton ...");
  }

  @Override
  protected Optional<String> doInBackground(final Void... params) {
    final String datetime = SIMPLE_DATE_TIME_FORMAT.format(new Date());
    try {
      FileUtils.copyURLToFile(
          new URL(SOURCE_URL),
          new File(LOCAL_SWANTON_ZIP_DIR + datetime + ".zip"));
      return Optional.empty();
    } catch (final IOException e) {
      return Optional.of(e.toString());
    }
  }

  @Override
  protected void onPostExecute(final Optional<String> result) {
    if (result.isPresent()) {
      final String errorMsg =
          String.format("Error while downloading .zip from source: %s", result.get());

      Snackbar.make(this.view.get(), errorMsg, Snackbar.LENGTH_LONG).show();
      Log.e(Tags.MAIN_ACTIVITY.getTag(), errorMsg);
    } else {
      Snackbar.make(this.view.get(), "Downloaded source zipfile!", Snackbar.LENGTH_LONG).show();
      Log.i(Tags.MAIN_ACTIVITY.getTag(), "Downloaded source zipfile!");
    }
  }
}
