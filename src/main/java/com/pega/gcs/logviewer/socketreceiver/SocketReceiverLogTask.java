
package com.pega.gcs.logviewer.socketreceiver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.fringecommon.guiutilities.EventReadTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.FileReadTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.ReadCounterTaskInfo;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.fringecommon.utilities.KnuthMorrisPrattAlgorithm;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.parser.LogParser;

public class SocketReceiverLogTask extends SwingWorker<LogParser, ReadCounterTaskInfo> {

    private static final Log4j2Helper LOG = new Log4j2Helper(SocketReceiverLogTask.class);

    private static int DEFAULT_CHUNK_SIZE = 8;

    private int port;

    private LogTableModel logTableModel;

    private JProgressBar progressBar;

    private JLabel progressText;

    private AtomicLong totalLineCount;

    private int processedCount;

    private int errorCount;

    public SocketReceiverLogTask(int port, LogTableModel logTableModel, JProgressBar progressBar, JLabel progressText) {

        this.port = port;
        this.logTableModel = logTableModel;
        this.progressBar = progressBar;
        this.progressText = progressText;

        this.totalLineCount = new AtomicLong(0);
        this.processedCount = 0;
        this.errorCount = 0;
    }

    public long getTotalLinrCount() {
        return totalLineCount.get();
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    protected LogParser doInBackground() throws Exception {

        long before = System.currentTimeMillis();
        int readCounter = 0;

        LOG.info("SocketReceiverLogTask - Starting receiving on port " + port);

        AbstractLogPattern abstractLogPattern = logTableModel.getLogPattern();
        Charset charset = logTableModel.getCharset();
        Locale locale = logTableModel.getLocale();
        TimeZone displayTimezone = logTableModel.getLogTimeZone();

        LogParser logParser = LogParser.getLogParserFromPattern(abstractLogPattern, charset, locale, displayTimezone);

        updateLogTableModel(logParser);

        LOG.info("SocketReceiverLogTask - Using Charset: " + charset + " Locale: " + locale + " Display Timezone: "
                + displayTimezone);

        FileReadTaskInfo fileReadTaskInfo = new FileReadTaskInfo(0, 0);
        EventReadTaskInfo eventReadTaskInfo = new EventReadTaskInfo(0, 0);

        ReadCounterTaskInfo readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
        readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

        publish(readCounterTaskInfo);

        byte[] newLine = "\n".getBytes(charset);

        long startSeek = 0;
        long endSeek = 0;
        long totalread = 0;
        byte[] balanceByteArray = new byte[0];

        int buffSize = DEFAULT_CHUNK_SIZE * (int) FileUtils.ONE_MB;

        try (DatagramSocket datagramSocket = new DatagramSocket(port)) {

            datagramSocket.setReceiveBufferSize(buffSize);

            while (!isCancelled()) {

                byte[] byteBuffer = new byte[buffSize];

                DatagramPacket datagramPacket = new DatagramPacket(byteBuffer, buffSize);
                datagramSocket.receive(datagramPacket);

                // Future: may be collate from multiple nodes?
                // InetAddress senderAddress = datagramPacket.getAddress();

                byteBuffer = datagramPacket.getData();
                int readLen = datagramPacket.getLength();

                progressBar.setIndeterminate(false);

                readCounter++;

                int startidx = 0;

                int index = KnuthMorrisPrattAlgorithm.indexOfWithPatternLength(byteBuffer, newLine, startidx);

                if (index != -1) {

                    while ((!isCancelled()) && (index != -1)) {

                        endSeek = totalread + index;

                        long teseRead = endSeek - startSeek;

                        if (teseRead > Integer.MAX_VALUE) {
                            teseRead = Integer.MAX_VALUE;
                        }

                        byte[] teseByteBuffer = new byte[(int) teseRead];

                        // copy the balance byte array
                        int balanceLength = balanceByteArray.length;
                        System.arraycopy(balanceByteArray, 0, teseByteBuffer, 0, balanceLength);

                        // copy the remaining bytes
                        int remaininglength = ((int) teseRead - balanceByteArray.length);
                        System.arraycopy(byteBuffer, startidx, teseByteBuffer, balanceLength, remaininglength);

                        // once copied, reset the balance array
                        balanceByteArray = new byte[0];

                        String logEntryStr = new String(teseByteBuffer, charset);

                        logEntryStr = GeneralUtilities.specialTrim(logEntryStr);

                        totalLineCount.incrementAndGet();

                        logParser.parse(logEntryStr);

                        startSeek = endSeek;

                        startidx = index;

                        index = KnuthMorrisPrattAlgorithm.indexOfWithPatternLength(byteBuffer, newLine, startidx);

                    }
                }

                logParser.parseFinal();

                // in case the terminator string is not found in the current iterator, we need
                // to accumulate the previously read balanceByteArray in the balanceByteArray
                int existingBalanceByteArrayLength = balanceByteArray.length;

                int balance = readLen - startidx;

                byte[] newBalanceByteArray = new byte[balance + existingBalanceByteArrayLength];

                // first copy the existing balance array,
                if (existingBalanceByteArrayLength > 0) {
                    System.arraycopy(balanceByteArray, 0, newBalanceByteArray, 0, existingBalanceByteArrayLength);
                }

                System.arraycopy(byteBuffer, startidx, newBalanceByteArray, existingBalanceByteArrayLength, balance);

                balanceByteArray = newBalanceByteArray;

                totalread = totalread + readLen;

                int rowCount = (logParser != null) ? logParser.getLogEntryModel().getTotalRowCount() : 0;

                fileReadTaskInfo = new FileReadTaskInfo(totalread, totalread);
                eventReadTaskInfo = new EventReadTaskInfo(rowCount, totalLineCount.get());

                readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
                readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

                publish(readCounterTaskInfo);

                // this should be last step.
                logTableModel.fireTableDataChanged();

            } // outer while

        } finally {

            long diff = System.currentTimeMillis() - before;

            int secs = (int) Math.ceil((double) diff / 1E3);

            int count = (logParser != null) ? logParser.getLogEntryModel().getTotalRowCount() : 0;

            LOG.info("Loaded " + count + " log events in " + secs + " secs " + " readCounter: " + readCounter);
        }

        return logParser;
    }

    private void updateLogTableModel(LogParser logParser) {

        LOG.info("Using parser " + logParser);

        LogEntryModel logEntryModel = logParser.getLogEntryModel();

        logTableModel.setLogEntryModel(logEntryModel);

    }

    @Override
    protected void process(List<ReadCounterTaskInfo> chunks) {

        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        Collections.sort(chunks);

        ReadCounterTaskInfo readCounterTaskInfo = chunks.get(chunks.size() - 1);

        FileReadTaskInfo fileReadTaskInfo = readCounterTaskInfo.getFileReadTaskInfo();
        EventReadTaskInfo eventReadTaskInfo = readCounterTaskInfo.getEventReadTaskInfo();

        long fileSize = fileReadTaskInfo.getFileSize();
        long fileRead = fileReadTaskInfo.getFileRead();

        long eventCount = eventReadTaskInfo.getEventCount();
        long linesRead = eventReadTaskInfo.getLinesRead();

        int progress = 0;

        if (fileSize > 0) {
            progress = (int) ((fileRead * 100) / fileSize);
        }

        // because the process() happen asynchronous to doInBackground().
        // This flag is to make sure changes in doInBackground() doesn't get
        // overridden in process().
        boolean progressBarIndeterminate = progressBar.isIndeterminate();

        if (!progressBarIndeterminate) {
            progressBar.setString(null);
            progressBar.setValue(progress);
        }

        String message = String.format("Loaded %d log events (%d lines)", eventCount, linesRead);

        progressText.setText(message);

    }

}
