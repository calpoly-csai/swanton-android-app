package com.csai.swanton.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public final class ChannelSftpFactory {
  private static final String REMOTE_HOST = "192.168.1.22";
  private static final String USERNAME = "pi";
  private static final String PASSWORD = "raspberrypi";
  private static final String SFTP_CHANNEL_TYPE = "sftp";

  public static ChannelSftp create() {
    try {
      final Session session = new JSch().getSession(USERNAME, REMOTE_HOST);
      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(PASSWORD);
      session.connect();

      final ChannelSftp channelSftp = (ChannelSftp) session.openChannel(SFTP_CHANNEL_TYPE);
      channelSftp.connect();

      return channelSftp;
    } catch (final JSchException e) {
      throw new RuntimeException(e);
    }
  }

  private ChannelSftpFactory() {}
}
