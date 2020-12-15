package com.csai.swanton.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.csai.swanton.sftp.ChannelSftpHandler;
import com.csai.swanton.util.ActivityTags.Tags;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;

/**
 * Asynchronous task for uploading the swanton app source code.
 */
@AllArgsConstructor
public class UploadSourceTask extends AsyncTask<Void, Void, Optional<String>> {
  public static final String LOCAL_SWANTON_ZIP_DIR = "./sdcard/Swanton/zip/";

  @Nonnull
  private final WeakReference<View> view;
  @Nonnull
  private final Optional<ChannelSftpHandler> handler;

  @Override
  protected void onPreExecute() {
    Snackbar.make(
        this.view.get(),
        "Uploading .zip to raspberry pi...",
        Snackbar.LENGTH_SHORT).show();
    Log.i(
        Tags.MAIN_ACTIVITY.getTag(),
        "Uploading .zip to raspberry pi...");
  }

  @Override
  protected Optional<String> doInBackground(final Void... params) {
    if (this.handler.isPresent()) {
      try {
        final File sourceZip =
            new File(LOCAL_SWANTON_ZIP_DIR + DownloadSourceTask.ZIPFILE_NAME);

        if (sourceZip.exists()) {
          this.handler.get().upload(sourceZip);

          // I dont want someone to accidentally upload source zip that is old/they don't want
          // to upload, so I'm deleting the file after it's uploaded successfully. The only
          // potential error here is if the delete fails, but if we've gotten this far then we should
          // have permissions to read/write so the delete shouldn't fail.
          Log.i(Tags.MAIN_ACTIVITY.getTag(), "Deleting source zipfile...");
          sourceZip.delete();
          Log.i(Tags.MAIN_ACTIVITY.getTag(), "Successfully deleted source zipfile.");
          return Optional.empty();
        } else {
          return Optional.of("No source zipfile found, try downloading from source.");
        }
      } catch (final Exception e) {
        return Optional.of(e.toString());
      }
    } else {
      return Optional.of("Not connected to RPi.");
    }
  }

  @Override
  protected void onPostExecute(final Optional<String> result) {
    if (result.isPresent()) {
      final String errorMsg =
          String.format("Error while uploading .zip: %s", result.get());

      Snackbar.make(this.view.get(), errorMsg, Snackbar.LENGTH_LONG).show();
      Log.e(Tags.MAIN_ACTIVITY.getTag(), errorMsg);
    } else {
      Snackbar.make(this.view.get(), "Uploaded source zipfile!", Snackbar.LENGTH_LONG).show();
      Log.i(Tags.MAIN_ACTIVITY.getTag(), "Uploaded source zipfile!");
    }
  }
}
