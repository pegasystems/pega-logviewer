/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.EventReadTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.FileReadByteArray;
import com.pega.gcs.fringecommon.guiutilities.FileReadTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.FileReaderThread;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ReadCounterTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.fringecommon.utilities.KnuthMorrisPrattAlgorithm;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.parser.LogParser;

/**
 * Show progress monitor at the beginning for the first load, in case of tail, for subsequent load, only update the progressbar.
 */
public class LogFileLoadTask extends SwingWorker<LogParser, ReadCounterTaskInfo> {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogFileLoadTask.class);

    private Component parent;

    private ModalProgressMonitor modalProgressMonitor;

    private JProgressBar progressBar;

    private JLabel progressText;

    private LogTableModel logTableModel;

    private FileReaderThread fileReaderThread;

    private boolean tailLogFile;

    private LogParser logParser;

    private AtomicLong totalLineCount;

    private int processedCount;

    private int errorCount;

    private boolean initialLoad;

    public LogFileLoadTask(Component parent, LogTableModel logTableModel, LogViewerSetting logViewerSetting,
            ModalProgressMonitor modalProgressMonitor, JProgressBar progressBar, JLabel progressText) {

        this.parent = parent;
        this.logTableModel = logTableModel;
        this.fileReaderThread = null;

        this.modalProgressMonitor = modalProgressMonitor;
        this.progressBar = progressBar;
        this.progressText = progressText;
        this.initialLoad = true;

        this.totalLineCount = new AtomicLong(0);
        this.processedCount = 0;
        this.errorCount = 0;

        this.tailLogFile = logViewerSetting.isTailLogFile();
        this.logParser = getLogParserFromRecentFile();

        // in case of alert file, there is no log pattern and we need to parse
        // the file anyways to get the column list
        if (logParser != null) {

            AbstractLogPattern abstractLogPattern = logParser.getLogPattern();

            if (abstractLogPattern != null) {
                LOG.info("Using Log Pattern: " + abstractLogPattern);
                LogEntryModel logEntryModel;
                logEntryModel = logParser.getLogEntryModel();
                logTableModel.setLogEntryModel(logEntryModel);
            } else {
                logParser = null;
            }
        }
    }

    public long getTotalLineCount() {
        return totalLineCount.get();
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected LogParser doInBackground() throws Exception {

        long before = System.currentTimeMillis();
        int readCounter = 0;

        Charset charset = logTableModel.getCharset();
        Locale locale = logTableModel.getLocale();
        TimeZone displayTimezone = logTableModel.getLogTimeZone();

        String filePath = logTableModel.getFilePath();

        File logFile = new File(filePath);

        LOG.info("LogFileLoadTask - Using Charset: " + charset + " Locale: " + locale + " Display Timezone: "
                + displayTimezone);
        LOG.info("LogFileLoadTask - Loading file: " + logFile);

        FileReadTaskInfo fileReadTaskInfo = new FileReadTaskInfo(0, 0);
        EventReadTaskInfo eventReadTaskInfo = new EventReadTaskInfo(0, 0);

        ReadCounterTaskInfo readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
        readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

        publish(readCounterTaskInfo);

        AtomicBoolean cancel = new AtomicBoolean(false);

        int fileReadQueueCapacity = 5;

        LinkedBlockingQueue<FileReadByteArray> fileReadQueue;
        fileReadQueue = new LinkedBlockingQueue<FileReadByteArray>(fileReadQueueCapacity);

        AtomicLong fileSize = new AtomicLong(-1);

        try {

            // use the tail mode for file read
            fileReaderThread = new FileReaderThread(logFile, fileSize, fileReadQueue, tailLogFile, cancel);
            Thread frtThread = new Thread(fileReaderThread);
            frtThread.start();

            byte[] newLine = "\n".getBytes(charset);

            long totalread = 0;
            byte[] balanceByteArray = new byte[0];
            List<String> readLineList = new ArrayList<String>();

            while (!isCancelled()) {

                if (initialLoad && modalProgressMonitor.isCanceled()) {
                    cancel.set(true);
                    cancel(true);
                    break;
                }

                FileReadByteArray frba = null;

                try {
                    frba = fileReadQueue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException ie) {
                    LOG.error("fileReadQueue InterruptedException ", ie);
                }

                if (frba != null) {

                    byte[] byteBuffer = frba.getBytes();
                    int readLen = byteBuffer.length;

                    LOG.trace("FileReadByteArray recieved bytes - readLen: " + readLen);

                    if (readLen > 0) {

                        progressBar.setIndeterminate(false);

                        readCounter++;

                        int startidx = 0;
                        int index = -1;

                        // copy the balance byte array
                        int balanceByteArrayLength = balanceByteArray.length;

                        if (balanceByteArrayLength > 0) {

                            int newSize = readLen + balanceByteArrayLength;

                            byte[] byteBufferNew = new byte[newSize];

                            // copy the balance to the beginning of new array.
                            System.arraycopy(balanceByteArray, 0, byteBufferNew, 0, balanceByteArrayLength);

                            // copy the newly read byteBuffer to the remaining space
                            System.arraycopy(byteBuffer, 0, byteBufferNew, balanceByteArrayLength, readLen);

                            byteBuffer = byteBufferNew;

                            // once copied, reset the balance array
                            balanceByteArray = new byte[0];

                        }

                        index = KnuthMorrisPrattAlgorithm.indexOfWithPatternLength(byteBuffer, newLine, startidx);

                        if (index != -1) {

                            while ((!isCancelled()) && (index != -1)) {

                                int teseRead = index - startidx;

                                byte[] teseByteBuffer = new byte[teseRead];

                                System.arraycopy(byteBuffer, startidx, teseByteBuffer, 0, teseRead);

                                String logEntryStr = new String(teseByteBuffer, charset);

                                logEntryStr = GeneralUtilities.specialTrim(logEntryStr);

                                totalLineCount.incrementAndGet();

                                // adding intelligent parsing. accumulate 100 lines and attempt to get a parser for these lines.
                                // in case of alert file, there is no log pattern and we need to parse the file anyways to get the column list

                                if ((!isCancelled()) && (logParser == null)) {

                                    readLineList.add(logEntryStr);

                                    if (readLineList.size() >= 200) {
                                        logParser = getLogParser(logFile.getName(), readLineList, charset, locale,
                                                displayTimezone);

                                        if (logParser == null) {
                                            cancel(true);
                                        } else {
                                            updateLogTableModel(logParser);
                                        }
                                    }
                                } else {
                                    logParser.parse(logEntryStr);
                                }

                                startidx = index;

                                index = KnuthMorrisPrattAlgorithm.indexOfWithPatternLength(byteBuffer, newLine,
                                        startidx);

                            }
                        }

                        int byteBufferlen = byteBuffer.length;
                        int balance = byteBufferlen - startidx;

                        balanceByteArray = new byte[balance];

                        System.arraycopy(byteBuffer, startidx, balanceByteArray, 0, balance);

                        totalread = totalread + readLen;

                        LOG.trace("No more new lines - totalread: " + totalread + " totalLineCount: " + totalLineCount
                                + " balanceByteArray.length: " + balanceByteArray.length);

                        // in case the number of line in the file is less than 100, the logParser would not have initialised.
                        if ((!isCancelled()) && (logParser == null)) {

                            logParser = getLogParser(logFile.getName(), readLineList, charset, locale, displayTimezone);

                            if (logParser == null) {
                                cancel(true);
                            } else {
                                updateLogTableModel(logParser);
                            }
                        }

                        int rowCount = (logParser != null) ? logParser.getLogEntryModel().getTotalRowCount() : 0;

                        fileReadTaskInfo = new FileReadTaskInfo(fileSize.get(), totalread);
                        eventReadTaskInfo = new EventReadTaskInfo(rowCount, totalLineCount.get());

                        readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
                        readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

                        publish(readCounterTaskInfo);

                    } else {
                        // empty bytebuffer is placed at the end of every file
                        // read iteration.
                        LOG.info("Received poison pill - reset counters");

                        if (logParser != null) {

                            if (balanceByteArray.length > 0) {

                                String logEntryStr = new String(balanceByteArray, charset);

                                logEntryStr = GeneralUtilities.specialTrim(logEntryStr);

                                logParser.parse(logEntryStr);
                            }

                            logParser.parseFinal();

                            int rowCount = (logParser != null) ? logParser.getLogEntryModel().getTotalRowCount() : 0;

                            fileReadTaskInfo = new FileReadTaskInfo(fileSize.get(), totalread);
                            eventReadTaskInfo = new EventReadTaskInfo(rowCount, totalLineCount.get());

                            readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
                            readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

                            publish(readCounterTaskInfo);

                            processedCount = logParser.getProcessedCount();
                        }

                        // reset counters

                        totalread = 0;

                        if (tailLogFile) {
                            progressBar.setValue(0);
                            progressBar.setIndeterminate(true);
                            progressBar.setString("Waiting...");
                        }

                        RecentFile recentFile = logTableModel.getRecentFile();
                        recentFile.setAttribute(RecentFile.KEY_SIZE, fileSize.get());

                        String fileSHA = fileReaderThread.getSHA();
                        recentFile.setAttribute(RecentFile.KEY_SHA, fileSHA);

                        Message.MessageType messageType = MessageType.INFO;

                        StringBuilder messageB = new StringBuilder();
                        messageB.append(logFile.getAbsolutePath());
                        messageB.append(" (");
                        messageB.append(totalLineCount.get());
                        messageB.append(" lines). ");

                        messageB.append("Processed ");
                        messageB.append(processedCount);
                        messageB.append(" new log events.");

                        if (errorCount > 0) {

                            messageType = MessageType.ERROR;

                            messageB.append(" ");
                            messageB.append(errorCount);
                            messageB.append(" Error");
                            messageB.append((errorCount > 1 ? "s" : ""));
                            messageB.append(" while loading log file");

                        }

                        // remove progress monitor dialog after initial load
                        if (initialLoad) {

                            // fixing the issue where the progress monitor hangs
                            // when close() is called.
                            try {
                                SwingUtilities.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        LOG.info("Closing Monitor");
                                        modalProgressMonitor.close();
                                    }
                                });
                            } catch (Throwable t) {
                                LOG.info("Got error " + t.getMessage());
                            }

                            // after initial load, update the recent file to set
                            // timezone etc.
                            updateRecentFile();

                            initialLoad = false;
                        }

                        Message message = new Message(messageType, messageB.toString());

                        logTableModel.setMessage(message);

                        LOG.debug("Calling fireTableDataChanged");
                        // this should be last step.
                        logTableModel.fireTableDataChanged();

                    }
                } else {
                    if (!frtThread.isAlive()) {

                        LOG.info("File reader Thread finished. Breaking LogFileTask");

                        stopTailing();

                        // file reader finished. exit this.
                        break;
                    }
                }
            } // outer while

            if (isCancelled() || (!frtThread.isAlive())) {
                // handle cancel operation
                cancel.set(true);
            }

        } finally {
            cancel.set(true);

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

    private void updateRecentFile() {

        if (logParser != null) {

            LOG.info("Updating recent file");

            RecentFile recentFile = logTableModel.getRecentFile();

            AbstractLogPattern abstractLogPattern = logParser.getLogPattern();

            recentFile.setAttribute(RecentFile.KEY_LOGFILETYPE, abstractLogPattern);

            LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

            TimeZone displayDateFormatTimeZone = logEntryModel.getDisplayDateFormatTimeZone();

            recentFile.setAttribute(RecentFile.KEY_TIMEZONE, displayDateFormatTimeZone);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
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

        boolean progressBarIndeterminate = progressBar.isIndeterminate();

        if (!progressBarIndeterminate) {
            progressBar.setString(null);
            progressBar.setValue(progress);
        }

        if (initialLoad) {

            String message = String.format("Loaded %d log events (%d lines) (%d%%)", eventCount, linesRead, progress);

            progressText.setText(message);

            modalProgressMonitor.setProgress(progress);
            modalProgressMonitor.setNote(message);

        } else {

            String message = String.format("Loaded %d log events (%d lines)", eventCount, linesRead);

            progressText.setText(message);
        }
    }

    private LogParser getLogParserFromRecentFile() {

        LogParser logParser = null;

        AbstractLogPattern abstractLogPattern = logTableModel.getLogPattern();

        if (abstractLogPattern != null) {
            // get values from saved pref data
            Charset charset = logTableModel.getCharset();
            Locale locale = logTableModel.getLocale();
            TimeZone displayTimezone = logTableModel.getLogTimeZone();

            logParser = LogParser.getLogParser(abstractLogPattern, charset, locale, displayTimezone);
        }

        return logParser;

    }

    private LogParser getLogParser(String fileName, List<String> readLineList, Charset charset, Locale locale,
            TimeZone displayTimezone) {

        LogParser aiLogParser = null;

        aiLogParser = LogParser.getLogParser(fileName, readLineList, charset, locale, displayTimezone);

        if (aiLogParser == null) {
            // ask user for pattern
            // open dialog to user
            LogPatternSelectionDialog lpsd = new LogPatternSelectionDialog(readLineList, charset, locale,
                    displayTimezone, BaseFrame.getAppIcon(), parent);

            lpsd.setVisible(true);

            aiLogParser = lpsd.getLogParser();

        }

        // if its still empty cancel out
        if (aiLogParser == null) {
            cancel(true);
        }

        return aiLogParser;
    }

    public void stopTailing() {

        if (fileReaderThread != null) {
            fileReaderThread.stopTailing();
        }

        tailLogFile = false;

        progressBar.setIndeterminate(false);
        progressBar.setString(null);
        progressBar.setValue(100);

    }

    public String getFileSHA() {

        String fileSHA = null;

        if (fileReaderThread != null) {
            fileSHA = fileReaderThread.getSHA();
        }

        return fileSHA;
    }
}
