package com.csai.swanton.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.csai.swanton.sftp.factory.ChannelSftpFactory;
import com.csai.swanton.util.ActivityTags.Tags;
import com.google.android.material.snackbar.Snackbar;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import java.lang.ref.WeakReference;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;

/**
 * Asynchronous task for connecting to a raspberry pi.
 */
@AllArgsConstructor
public class ConnectTask extends AsyncTask<Void, Void, Optional<ChannelSftp>> {

  @Nonnull
  private final WeakReference<View> view;

  @Override
  protected void onPreExecute() {
    Snackbar.make(this.view.get(), "Connecting to Raspberry Pi...", Snackbar.LENGTH_SHORT).show();
    Log.i(Tags.MAIN_ACTIVITY.getTag(), "Connecting to Raspberry Pi...");
  }

  @Override
  protected Optional<ChannelSftp> doInBackground(final Void... params) {
    try {
      final Optional<ChannelSftp> channelSftp = Optional.of(ChannelSftpFactory.create());
      return channelSftp;
    } catch (final JSchException e) {
      // Swallow exception here and just log so we don't crash.
      Log.e(Tags.MAIN_ACTIVITY.getTag(), "Failed to connect to Raspberry Pi: " + e);
      return Optional.empty();
    }
  }

  @Override
  protected void onPostExecute(final Optional<ChannelSftp> channelSftp) {
    if (channelSftp.isPresent()) {
      Snackbar.make(
          this.view.get(), "Connected to Raspberry Pi!", Snackbar.LENGTH_LONG).show();
      Log.i(Tags.MAIN_ACTIVITY.getTag(), "Connected to Raspberry Pi.");
    } else {
      Snackbar.make(
          this.view.get(), "Failed to connect to Raspberry Pi.", Snackbar.LENGTH_LONG).show();
    }
  }
}
