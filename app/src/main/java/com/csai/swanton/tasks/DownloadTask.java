package com.csai.swanton.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.csai.swanton.sftp.ChannelSftpHandler;
import com.csai.swanton.util.ActivityTags.Tags;
import com.google.android.material.snackbar.Snackbar;
import java.lang.ref.WeakReference;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;

/**
 * Asynchronous task for downloading from a raspberry pi.
 */
@AllArgsConstructor
public class DownloadTask extends
    AsyncTask<Void, Void, Optional<String>> {

  @Nonnull
  private final WeakReference<View> view;
  @Nonnull
  private final Optional<ChannelSftpHandler> handler;

  @Override
  protected void onPreExecute() {
    Snackbar.make(this.view.get(), "Downloading from Raspberry Pi...", Snackbar.LENGTH_SHORT)
        .show();
    Log.i(Tags.MAIN_ACTIVITY.getTag(), "Downloading from Raspberry Pi...");
  }

  @Override
  protected Optional<String> doInBackground(final Void... params) {
    if (this.handler.isPresent()) {
      try {
        this.handler.get().download();
        return Optional.empty();
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
          String.format("Error while downloading from Raspberry Pi: %s", result.get());

      Snackbar.make(this.view.get(), errorMsg, Snackbar.LENGTH_SHORT).show();
      Log.e(Tags.MAIN_ACTIVITY.getTag(), errorMsg);
    } else {
      Snackbar.make(this.view.get(), "Downloaded from Raspberry Pi!", Snackbar.LENGTH_SHORT).show();
      Log.i(Tags.MAIN_ACTIVITY.getTag(), "Downloaded from Raspberry Pi.");
    }
  }
}
