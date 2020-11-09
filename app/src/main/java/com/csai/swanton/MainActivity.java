package com.csai.swanton;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.csai.swanton.sftp.ChannelSftpFactory;
import com.csai.swanton.sftp.ChannelSftpHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.jcraft.jsch.ChannelSftp;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
  public static final String TAG = "MainActivity";
  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static final String[] PERMISSIONS_STORAGE = {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  private ChannelSftpHandler channelSftpHandler;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    verifyStoragePermissions(this);

    final Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    final FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(
        view -> {
          Snackbar.make(view, "Connecting to Raspberry Pi...", Snackbar.LENGTH_SHORT).show();
          Log.i(TAG, "Connecting to Raspberry Pi...");

          try {
            this.channelSftpHandler = new ChannelSftpHandler(new ConnectTask().execute().get());
          } catch (final ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
          }

          Snackbar.make(view, "Connected to Raspberry Pi!", Snackbar.LENGTH_SHORT).show();
          Log.i(TAG, "Connected to Raspberry Pi...");
        });

    final FloatingActionButton fab2 = findViewById(R.id.fab2);
    fab2.setOnClickListener(
        view -> {
          Snackbar.make(view, "Downloading from Raspberry Pi...", Snackbar.LENGTH_SHORT).show();
          Log.i(TAG, "Downloading from Raspberry Pi...");

          this.channelSftpHandler.download();

          Snackbar.make(view, "Downloaded from Raspberry Pi!", Snackbar.LENGTH_SHORT).show();
          Log.i(TAG, "Downloaded from Raspberry Pi...");
        }
    );
  }

  private class ConnectTask extends AsyncTask<Void, Void, ChannelSftp> {
    @Override
    protected ChannelSftp doInBackground(final Void... params) {
      return ChannelSftpFactory.create();
    }
  }

  /**
   * Verifies that an Activity has permissions to read and write to external storage.
   *
   * @param activity The activity to verify permissions for.
   */
  public static void verifyStoragePermissions(final Activity activity) {
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