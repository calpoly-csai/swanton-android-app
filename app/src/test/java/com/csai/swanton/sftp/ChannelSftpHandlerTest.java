package com.csai.swanton.sftp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import java.util.List;
import java.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChannelSftpHandlerTest {
  private static final String LS_ENTRY_ONE_FILENAME = "testFilenameOne";
  private static final String LS_ENTRY_TWO_FILENAME = "testFilenameTwo";
  private static final String CURRENT_FOLDER_FILENAME = ".";
  private static final String PARENT_FOLDER_FILENAME = "..";

  @Mock private ChannelSftp mockChannelSftp;
  @Mock private LsEntry mockLsEntryOne;
  @Mock private LsEntry mockLsEntryTwo;
  @Mock private LsEntry mockLsEntryCurrentFolder;
  @Mock private LsEntry mockLsEntryPreviousFolder;
  @Captor private ArgumentCaptor<String> srcPathCaptor;
  @Captor private ArgumentCaptor<String> dstpathCaptor;

  @Before
  public void setup() {
    when(mockLsEntryOne.getFilename()).thenReturn(LS_ENTRY_ONE_FILENAME);
    when(mockLsEntryTwo.getFilename()).thenReturn(LS_ENTRY_TWO_FILENAME);
    when(mockLsEntryCurrentFolder.getFilename()).thenReturn(CURRENT_FOLDER_FILENAME);
    when(mockLsEntryPreviousFolder.getFilename()).thenReturn(PARENT_FOLDER_FILENAME);
  }

  @Test
  public void testDownload() throws SftpException {
    final Vector<LsEntry> expectedLsEntries = new Vector<>(4);
    expectedLsEntries.add(mockLsEntryOne);
    expectedLsEntries.add(mockLsEntryTwo);
    expectedLsEntries.add(mockLsEntryCurrentFolder);
    expectedLsEntries.add(mockLsEntryPreviousFolder);

    when(mockChannelSftp.ls(ChannelSftpHandler.REMOTE_SWANTON_LOGS_DIR))
        .thenReturn(expectedLsEntries);

    new ChannelSftpHandler(mockChannelSftp).download();

    // Make sure only the valid LsEntries were attmpted to be downloaded
    verify(mockChannelSftp, times(expectedLsEntries.size() - 2))
        .get(srcPathCaptor.capture(), dstpathCaptor.capture());

    final List<String> srcPaths = srcPathCaptor.getAllValues();
    final List<String> dstPaths = dstpathCaptor.getAllValues();

    // Verify that the correct source and destination paths were used for the .get() calls
    assertEquals(
        ImmutableList.of(
            ChannelSftpHandler.REMOTE_SWANTON_LOGS_DIR + LS_ENTRY_ONE_FILENAME,
            ChannelSftpHandler.REMOTE_SWANTON_LOGS_DIR + LS_ENTRY_TWO_FILENAME),
        srcPaths);
    assertEquals(
        ImmutableList.of(
            ChannelSftpHandler.LOCAL_SWANTON_LOGS_DIR + LS_ENTRY_ONE_FILENAME,
            ChannelSftpHandler.LOCAL_SWANTON_LOGS_DIR + LS_ENTRY_TWO_FILENAME),
        dstPaths);
  }
}
