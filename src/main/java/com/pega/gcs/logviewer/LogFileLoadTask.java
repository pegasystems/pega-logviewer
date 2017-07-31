/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.Component;
import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import com.pega.gcs.fringecommon.utilities.KnuthMorrisPrattAlgorithm;
import com.pega.gcs.logviewer.logfile.LogFileType;
import com.pega.gcs.logviewer.logfile.LogPattern;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.parser.LogParser;

/**
 * show progress monitor at the beginning for the first load, in case of tail,
 * for subsequent load, only update the progressbar.
 */
public class LogFileLoadTask extends SwingWorker<LogParser, ReadCounterTaskInfo> {

	private static final Log4j2Helper LOG = new Log4j2Helper(LogFileLoadTask.class);

	private Component parent;

	private ModalProgressMonitor mProgressMonitor;

	private JProgressBar progressBar;

	private JLabel progressText;

	private JLabel errorText;

	private LogTableModel logTableModel;

	private Set<LogPattern> pegaRuleslog4jPatternSet;

	private FileReaderThread fileReaderThread;

	private boolean tailLogFile;

	private LogParser logParser;

	private AtomicLong totalLineCount;

	private int processedCount;

	private int errorCount;

	private boolean initialLoad;

	public LogFileLoadTask(Component parent, LogTableModel logTableModel, Set<LogPattern> pegaRuleslog4jPatternSet,
			boolean tailLogFile, ModalProgressMonitor mProgressMonitor, JProgressBar progressBar, JLabel progressText,
			JLabel errorText) {

		this.parent = parent;
		this.logTableModel = logTableModel;
		this.pegaRuleslog4jPatternSet = pegaRuleslog4jPatternSet;
		this.fileReaderThread = null;
		this.tailLogFile = tailLogFile;

		this.mProgressMonitor = mProgressMonitor;
		this.progressBar = progressBar;
		this.progressText = progressText;
		this.errorText = errorText;
		this.initialLoad = true;

		this.totalLineCount = new AtomicLong(0);
		this.processedCount = 0;
		this.errorCount = 0;

		this.logParser = getLogParser();

		// in case of alert file, there is no log pattern and we need to parse
		// the file anyways to get the column list
		if (logParser != null) {

			LogFileType logFileType = logParser.getLogFileType();

			if ((logFileType != null) && (logFileType.getLogPattern() != null)) {
				LOG.info("Using Log Pattern: " + logFileType.getLogPattern().getLogPatternString());
				LogEntryModel logEntryModel;
				logEntryModel = logParser.getLogEntryModel();
				logTableModel.setLogEntryModel(logEntryModel);
			} else {
				logParser = null;
			}
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected LogParser doInBackground() throws Exception {

		long before = System.currentTimeMillis();
		int readCounter = 0;

		String charset = logTableModel.getCharset();
		Locale locale = logTableModel.getLocale();
		TimeZone displayTimezone = logTableModel.getLogTimeZone();

		RecentFile recentFile = logTableModel.getRecentFile();
		String logFilePath = (String) recentFile.getAttribute(RecentFile.KEY_FILE);

		File logFile = new File(logFilePath);

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

			byte[] newLine = "\n".getBytes();

			long startSeek = 0;
			long endSeek = 0;
			long totalread = 0;
			byte[] balanceByteArray = new byte[0];
			List<String> readLineList = new LinkedList<String>();

			while (!isCancelled()) {

				if (initialLoad && mProgressMonitor.isCanceled()) {
					cancel.set(true);
					cancel(true);
					break;
				}

				FileReadByteArray frba = null;

				try {
					frba = fileReadQueue.poll(1, TimeUnit.SECONDS);
				} catch (InterruptedException ie) {
					// ignore InterruptedException
				}

				if (frba != null) {

					byte[] byteBuffer = frba.getBytes();
					int readLen = byteBuffer.length;

					if (readLen > 0) {

						progressBar.setIndeterminate(false);

						readCounter++;

						int startidx = 0;
						int index = -1;

						index = KnuthMorrisPrattAlgorithm.indexOfWithPatternLength(byteBuffer, newLine, startidx);

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

								// logEntryStr =
								// logEntryStr.replaceAll("[\r\n]",
								// "");

								// replacing special characters at the eol.
								// however keeping space intact
								int origLength = logEntryStr.length();
								int length = origLength;

								while ((length > 0) && (logEntryStr.charAt(length - 1) < ' ')) {
									length--;
								}

								if (length < origLength) {
									logEntryStr = logEntryStr.substring(0, length);
								}

								totalLineCount.incrementAndGet();

								// adding intelligent parsing. accumulate 100
								// lines
								// and attempt to get a parser for these lines.
								// in case of alert file, there is no log
								// pattern and we need to parse the file anyways
								// to get the column list

								if ((!isCancelled()) && (logParser == null)) {

									readLineList.add(logEntryStr);

									if (readLineList.size() >= 100) {
										logParser = getLogParser(logFile.getName(), readLineList, locale,
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

								startSeek = endSeek;

								startidx = index;

								index = KnuthMorrisPrattAlgorithm.indexOfWithPatternLength(byteBuffer, newLine,
										startidx);

							}
						}

						// in case the terminator string is not found in the
						// current
						// iterator, we need to accumulate the previously read
						// balanceByteArray in the balanceByteArray
						int existingBalanceByteArrayLength = balanceByteArray.length;

						int balance = readLen - startidx;

						byte[] newBalanceByteArray = new byte[balance + existingBalanceByteArrayLength];

						// first copy the existing balance array,
						if (existingBalanceByteArrayLength > 0) {
							System.arraycopy(balanceByteArray, 0, newBalanceByteArray, 0,
									existingBalanceByteArrayLength);
						}

						System.arraycopy(byteBuffer, startidx, newBalanceByteArray, existingBalanceByteArrayLength,
								balance);

						balanceByteArray = newBalanceByteArray;

						totalread = totalread + readLen;

						if ((!isCancelled()) && (logParser == null)) {

							logParser = getLogParser(logFile.getName(), readLineList, locale, displayTimezone);

							updateLogTableModel(logParser);
						}

						// // set progress bar value
						// int progress = (int) ((totalread * 100) / totalSize);

						int rowCount = (logParser != null) ? logParser.getLogEntryModel().getTotalRowCount() : 0;

						fileReadTaskInfo = new FileReadTaskInfo(fileSize.get(), totalread);
						eventReadTaskInfo = new EventReadTaskInfo(rowCount, totalLineCount.get());

						readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
						readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

						publish(readCounterTaskInfo);

					} else {
						// empty bytebuffer is placed at the end of every file
						// read iteration.

						logParser.parseFinal();

						int rowCount = (logParser != null) ? logParser.getLogEntryModel().getTotalRowCount() : 0;

						fileReadTaskInfo = new FileReadTaskInfo(fileSize.get(), totalread);
						eventReadTaskInfo = new EventReadTaskInfo(rowCount, totalLineCount.get());

						readCounterTaskInfo = new ReadCounterTaskInfo(fileReadTaskInfo);
						readCounterTaskInfo.setEventReadTaskInfo(eventReadTaskInfo);

						publish(readCounterTaskInfo);

						// reset counters
						startSeek = 0;
						endSeek = 0;
						totalread = 0;

						processedCount = logParser.getProcessedCount();

						if (tailLogFile) {
							progressBar.setValue(0);
							progressBar.setIndeterminate(true);
							progressBar.setString("Waiting...");
						}

						recentFile.setAttribute(RecentFile.KEY_SIZE, fileSize.get());

						Message.MessageType messageType = MessageType.INFO;

						StringBuffer messageB = new StringBuffer();
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
										mProgressMonitor.close();
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

						LOG.info("Calling fireTableDataChanged");
						// this should be last step.
						logTableModel.fireTableDataChanged();

					}
				} else {
					if (!frtThread.isAlive()) {

						LOG.info("File reader Thread finished. Breaking LogFileTask");

						// file reader finished. exit this.
						break;
					}
				}
			}

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

			LogFileType logFileType = logParser.getLogFileType();

			recentFile.setAttribute(RecentFile.KEY_LOGFILETYPE, logFileType);

			LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

			DateFormat displayDateFormat = logEntryModel.getDisplayDateFormat();
			TimeZone dispTimeZone = displayDateFormat.getTimeZone();

			recentFile.setAttribute(RecentFile.KEY_TIMEZONE, dispTimeZone);
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

		// because the process() happen asynchronous to doInBackground().
		// This flag is to make sure changes in doInBackground() doesn't get
		// overridden in process().
		boolean progressBarIndeterminate = progressBar.isIndeterminate();

		if (!progressBarIndeterminate) {
			progressBar.setString(null);
			progressBar.setValue(progress);
		}

		if (initialLoad) {

			String message = String.format("Loaded %d log events (%d lines) (%d%%)", eventCount, linesRead, progress);

			progressText.setText(message);

			mProgressMonitor.setProgress(progress);
			mProgressMonitor.setNote(message);

		} else {

			String message = String.format("Loaded %d log events (%d lines)", eventCount, linesRead);

			progressText.setText(message);
		}
	}

	public LogParser getLogParser() {

		LogParser logParser = null;

		LogFileType logFileType = logTableModel.getLogFileType();

		if (logFileType != null) {
			// get values from saved pref data
			Locale locale = logTableModel.getLocale();
			TimeZone displayTimezone = logTableModel.getLogTimeZone();

			logParser = LogParser.getLogParser(logFileType, locale, displayTimezone);
		}

		return logParser;

	}

	private LogParser getLogParser(String fileName, List<String> readLineList, Locale locale,
			TimeZone displayTimezone) {

		LogParser aiLogParser = null;

		aiLogParser = LogParser.getLogParser(fileName, readLineList, pegaRuleslog4jPatternSet, locale, displayTimezone);

		if (aiLogParser == null) {
			// ask user for pattern
			// open dialog to user
			LogPatternSelectionDialog lpsd = new LogPatternSelectionDialog(readLineList, pegaRuleslog4jPatternSet,
					locale, displayTimezone, BaseFrame.getAppIcon(), parent);

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
}
