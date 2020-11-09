package com.csai.swanton.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChannelSftpHandler {
  @Nonnull
  private final ChannelSftp channelSftp;

  public void download() {
    try {
      System.err.println(this.channelSftp.ls("~/swanton/logs/"));
      //this.channelSftp.get("~/swanton/logs/file.txt", "/sdcard/Download/file.txt");
    } catch (final SftpException e) {
      throw new RuntimeException(e);
    }
  }
}
