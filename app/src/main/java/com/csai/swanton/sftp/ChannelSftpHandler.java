package com.csai.swanton.sftp;

import android.util.Log;
import com.csai.swanton.util.ActivityTags.Tags;
import com.csai.swanton.util.DirectoryHandler;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
  private static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.US);
  public static final String REMOTE_SWANTON_LOGS_DIR = "./swanton/logs/";
  public static final String REMOTE_SWANTON_ZIP_DIR = "./swanton/zip/";
  public static final String LOCAL_SWANTON_LOGS_DIR = "./sdcard/Swanton/logs/";

  @Nonnull
  private final ChannelSftp channelSftp;

  /**
   * Downloads log files from the raspberry pi to the local sdcard via SFTP.
   */
  public void download() {
    DirectoryHandler.createLocalDirectory(LOCAL_SWANTON_LOGS_DIR);

    try {
      final Vector<LsEntry> entries = this.channelSftp.ls(REMOTE_SWANTON_LOGS_DIR);

      Seq.seq(entries.stream())
          .map(LsEntry::getFilename)
          .filter(filename -> !filename.equals(".") && !filename.equals(".."))
          .forEach(filename -> {
            try {
              Log.i(
                  Tags.MAIN_ACTIVITY.getTag(),
                  String.format("Downloading: %s", REMOTE_SWANTON_LOGS_DIR + filename));

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
   * Uploads source .zip from local sdcard to raspberry pi via SFTP.
   * @param sourceZip The File object representing the source zipfile.
   */
  public void upload(final File sourceZip) {
    final String currentDateTime = SIMPLE_DATE_TIME_FORMAT.format(new Date());
    final String dst = REMOTE_SWANTON_ZIP_DIR + currentDateTime + ".zip";
    Log.i(Tags.MAIN_ACTIVITY.getTag(), String.format("Uploading zipfile to: %s", dst));

    try {
      this.channelSftp.put(new FileInputStream(sourceZip), dst);
    } catch (final SftpException | FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Disconnect the SFTP session.
   */
  public void close() {
    this.channelSftp.disconnect();
  }
}
