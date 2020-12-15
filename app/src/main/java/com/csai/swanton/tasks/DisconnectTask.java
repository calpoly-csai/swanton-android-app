package com.csai.swanton.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.csai.swanton.sftp.ChannelSftpHandler;
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
 * Asynchronous task for disconnecting from a raspberry pi.
 */
@AllArgsConstructor
public class DisconnectTask extends AsyncTask<Void, Void, Optional<String>> {

  @Nonnull
  private final WeakReference<View> view;
  @Nonnull
  private final Optional<ChannelSftpHandler> channelSftpHandler;

  @Override
  protected void onPreExecute() {
    Snackbar.make(this.view.get(), "Disconnecting from Raspberry Pi...", Snackbar.LENGTH_SHORT).show();
    Log.i(Tags.MAIN_ACTIVITY.getTag(), "Disconnecting from Raspberry Pi...");
  }

  @Override
  protected Optional<String> doInBackground(final Void... params) {
    if (this.channelSftpHandler.isPresent()) {
      this.channelSftpHandler.get().close();
      return Optional.empty();
    } else {
      return Optional.of("Not connected to Raspberry Pi, can't disconnect.");
    }
  }

  @Override
  protected void onPostExecute(final Optional<String> result) {
    if (result.isPresent()) {
      Snackbar.make(this.view.get(), result.get(), Snackbar.LENGTH_LONG).show();
      Log.e(Tags.MAIN_ACTIVITY.getTag(), "Not connected to Raspberry Pi, can't disconnect.");
    } else {
      Snackbar.make(
          this.view.get(), "Disconnected from Raspberry Pi.", Snackbar.LENGTH_LONG).show();
      Log.i(Tags.MAIN_ACTIVITY.getTag(), "Disconnected from Raspberry Pi.");
    }
  }
}
