package com.csai.swanton.sftp.factory;

import android.util.Log;
import com.csai.swanton.util.ActivityTags.Tags;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public final class ChannelSftpFactory {

  // TODO: Figure out credentials
  private static final String REMOTE_HOST = "192.168.1.22";
  private static final String USERNAME = "pi";
  private static final String PASSWORD = "raspberrypi";
  private static final String SFTP_CHANNEL_TYPE = "sftp";

  public static ChannelSftp create() throws JSchException {
    Log.i(
        Tags.MAIN_ACTIVITY.getTag(),
        String.format("Attempting to connect to %s as user: %s.", REMOTE_HOST, USERNAME));

    final Session session = new JSch().getSession(USERNAME, REMOTE_HOST);
    session.setConfig("StrictHostKeyChecking", "no");
    session.setPassword(PASSWORD);
    session.connect();

    final ChannelSftp channelSftp = (ChannelSftp) session.openChannel(SFTP_CHANNEL_TYPE);
    channelSftp.connect();

    return channelSftp;
  }

  private ChannelSftpFactory() {
  }
}
