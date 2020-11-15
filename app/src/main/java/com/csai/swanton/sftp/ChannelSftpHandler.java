package com.csai.swanton.sftp;

import android.util.Log;
import com.csai.swanton.util.ActivityTags.Tags;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.util.Vector;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.jooq.lambda.Seq;

/**
 * Class that contains the logic for downloading and uploading files to and from a raspberry pi via
 * SFTP connection.
 */
@AllArgsConstructor
public class ChannelSftpHandler {

  public static final String REMOTE_SWANTON_LOGS_DIR = "./swanton/logs/";
  public static final String LOCAL_SWANTON_LOGS_DIR = "./sdcard/Swanton/logs/";

  @Nonnull
  private final ChannelSftp channelSftp;

  /**
   * Downloads log files from the raspberry pi to the local sdcard via SFTP.
   */
  public void download() {
    createLocalSwantonDirectory();

    try {
      final Vector<LsEntry> entries = this.channelSftp.ls(REMOTE_SWANTON_LOGS_DIR);

      Seq.seq(entries.stream())
          .map(LsEntry::getFilename)
          .filter(filename -> !filename.equals(".") && !filename.equals(".."))
          .forEach(filename -> {
            try {
              this.channelSftp.get(
                  REMOTE_SWANTON_LOGS_DIR + filename,
                  LOCAL_SWANTON_LOGS_DIR + filename);
            } catch (final SftpException e) {
              throw new RuntimeException(e);
            }
          });

    } catch (final SftpException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a local swanton logs directory if it doesn't exist already.
   */
  private void createLocalSwantonDirectory() {
    try {
      final File swantonDir = new File(LOCAL_SWANTON_LOGS_DIR);
      if (!swantonDir.exists()) {
        swantonDir.mkdirs();
      }
    } catch (final Exception e) {
      Log.e(
          Tags.MAIN_ACTIVITY.getTag(),
          String.format("Could not find or create folder %s: %s", LOCAL_SWANTON_LOGS_DIR, e));
      throw new RuntimeException(e);
    }
  }
}
