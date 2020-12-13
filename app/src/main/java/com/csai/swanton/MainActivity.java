package com.csai.swanton;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.csai.swanton.sftp.ChannelSftpHandler;
import com.csai.swanton.tasks.ConnectTask;
import com.csai.swanton.tasks.DownloadLogsTask;
import com.csai.swanton.tasks.DownloadSourceTask;
import com.jcraft.jsch.ChannelSftp;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static final String[] PERMISSIONS_STORAGE = {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  private Optional<ChannelSftpHandler> channelSftpHandler = Optional.empty();

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    verifyStoragePermissions(this);

    final Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    findViewById(R.id.connect_button).setOnClickListener(
        view -> {
          final Optional<ChannelSftp> channelSftp;
          try {
            channelSftp = new ConnectTask(new WeakReference<>(view)).execute().get();
          } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
          }

          channelSftp.ifPresent(
              sftp -> this.channelSftpHandler = Optional.of(new ChannelSftpHandler(sftp)));
        });

    findViewById(R.id.download_logs_button).setOnClickListener(
        view -> {
          try {
            new DownloadLogsTask(new WeakReference<>(view), this.channelSftpHandler).execute().get();
          } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
          }
        });

    findViewById(R.id.download_source_button).setOnClickListener(
        view -> {
          try {
            new DownloadSourceTask(new WeakReference<>(view)).execute().get();
          } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Verifies that an Activity has permissions to read and write to external storage.
   *
   * @param activity The activity to verify permissions for.
   */
  private static void verifyStoragePermissions(final Activity activity) {
    int writePermission =
        ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    int readPermission =
        ActivityCompat.checkSelfPermission(activity, permission.READ_EXTERNAL_STORAGE);

    if (writePermission != PackageManager.PERMISSION_GRANTED
        || readPermission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(
          activity,
          PERMISSIONS_STORAGE,
          REQUEST_EXTERNAL_STORAGE
      );
    }
  }
}