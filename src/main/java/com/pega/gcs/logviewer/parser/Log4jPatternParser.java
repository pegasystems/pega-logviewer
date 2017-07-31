/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.core.pattern.ClassNamePatternConverter;
import org.apache.logging.log4j.core.pattern.DatePatternConverter;
import org.apache.logging.log4j.core.pattern.FileLocationPatternConverter;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.FullLocationPatternConverter;
import org.apache.logging.log4j.core.pattern.LevelPatternConverter;
import org.apache.logging.log4j.core.pattern.LineLocationPatternConverter;
import org.apache.logging.log4j.core.pattern.LineSeparatorPatternConverter;
import org.apache.logging.log4j.core.pattern.LiteralPatternConverter;
import org.apache.logging.log4j.core.pattern.LoggerPatternConverter;
import org.apache.logging.log4j.core.pattern.MapPatternConverter;
import org.apache.logging.log4j.core.pattern.MdcPatternConverter;
import org.apache.logging.log4j.core.pattern.MessagePatternConverter;
import org.apache.logging.log4j.core.pattern.MethodLocationPatternConverter;
import org.apache.logging.log4j.core.pattern.NdcPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.pattern.RelativeTimePatternConverter;
import org.apache.logging.log4j.core.pattern.SequenceNumberPatternConverter;
import org.apache.logging.log4j.core.pattern.ThreadNamePatternConverter;
import org.apache.logging.log4j.core.util.OptionConverter;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.fringecommon.utilities.TimeZoneUtil;
import com.pega.gcs.logviewer.logfile.LogFileType;
import com.pega.gcs.logviewer.logfile.LogPattern;
import com.pega.gcs.logviewer.model.Log4jLogEntry;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.Log4jLogExceptionEntry;
import com.pega.gcs.logviewer.model.Log4jLogRequestorLockEntry;
import com.pega.gcs.logviewer.model.Log4jLogSystemStartEntry;
import com.pega.gcs.logviewer.model.Log4jLogThreadDumpEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.SystemStart;

public class Log4jPatternParser extends LogParser {

	private static final Log4j2Helper LOG = new Log4j2Helper(Log4jPatternParser.class);

	private static final String NOSPACE_GROUP = "(\\s*?\\S*?\\s*?)";
	private static final String DEFAULT_GROUP = "(.*?)";
	private static final String GREEDY_GROUP = "(.*)";
	private static final String LIMITING_GROUP = "(.{n,m})"; // (\\s*?.{5,5})

	private static final long THREADDUMP_TIMESTAMP_INTERVAL = 5 * 60 * 1000;

	private String regExp;

	private Pattern linePattern;
	private Pattern requestorLockPattern;
	private Pattern exceptionPattern;
	private Pattern discardedMessagesPattern;
	private Pattern systemDatepattern;

	private ArrayList<String> logEntryColumnList;
	private ArrayList<String> logEntryColumnValueList;
	private String logEntryText;
	private ArrayList<String> additionalLines;

	private int lineCount;

	private int logEntryIndex;

	private String parseLine;
	private int parseLineCount;

	private Log4jLogEntryModel log4jLogEntryModel;

	private int levelIndex;
	private int timestampIndex;
	private int loggerIndex;
	private int messageIndex;

	private TimeZone modelTimezone;
	private TimeZone displayTimezone;

	private Date prevLogEntryDate;

	private int systemStartCounter;
	private SystemStart systemStart;

	private Log4jLogThreadDumpEntry log4jLogThreadDumpEntry;

	public Log4jPatternParser(LogFileType logFileType, Locale locale, TimeZone displayTimezone) {

		super(logFileType, locale);

		this.displayTimezone = displayTimezone;

		logEntryIndex = 0;
		parseLine = "";
		parseLineCount = 1;

		levelIndex = -1;
		timestampIndex = -1;

		modelTimezone = null;
		prevLogEntryDate = null;
		systemStartCounter = 1;
		systemStart = null;
		log4jLogThreadDumpEntry = null;

		logEntryColumnList = new ArrayList<String>();
		logEntryColumnValueList = new ArrayList<String>();
		logEntryText = null;
		additionalLines = new ArrayList<String>();

		String systemDateRegex = "System date:(?:.*?)\\d{2}\\:\\d{2}\\:\\d{2}(.*?)\\d{4}\\s+Total memory\\:(?:.*?)";
		systemDatepattern = Pattern.compile(systemDateRegex);

		String threadRegEx = "(.*)Unable to synchronize on requestor (.+?) within (\\d{1,19}) seconds: \\(thisThread = (.+?)\\) \\(originally locked by = (.+?)\\) \\(finally locked by = (.+?)\\)(.*)";

		requestorLockPattern = Pattern.compile(threadRegEx);
		// exceptionPattern = Pattern.compile("([\\w\\.]*Exception|Error)");
		exceptionPattern = Pattern.compile("([\\w\\.]+Exception)|([\\w\\.]+Error)");

		discardedMessagesPattern = Pattern.compile("Discarded (.+?) messages due to full event buffer(.*)");

		generateRegExp();

		log4jLogEntryModel = new Log4jLogEntryModel(getDateFormat(), getLocale(), displayTimezone);

		log4jLogEntryModel.setLogEntryColumnList(logEntryColumnList);

	}

	@Override
	public String toString() {
		return "Log4jPatternParser [regExp=" + regExp + ", log4jPattern=" + getLogFileType().getLogPattern() + "]";
	}

	private void generateRegExp() {

		lineCount = 0;
		regExp = "";

		LogFileType logFileType = getLogFileType();
		LogPattern logPattern = logFileType.getLogPattern();

		String logPatternStr = logPattern.getLogPatternString();
		String pattern = OptionConverter.convertSpecialChars(logPatternStr);

		LOG.info("generateRegExp - logPatternStr: " + logPatternStr);
		PatternParser patternParser = new PatternParser("Converter");

		List<PatternFormatter> patternFormatterList = patternParser.parse(pattern);

		logEntryColumnList.add(LogEntryColumn.LINE.getColumnId());
		logEntryColumnList.add(LogEntryColumn.DELTA.getColumnId());

		for (PatternFormatter patternFormatter : patternFormatterList) {

			String format = null;

			PatternConverter patternConverter = patternFormatter.getConverter();
			FormattingInfo formattingInfo = patternFormatter.getFormattingInfo();
			int minLength = formattingInfo.getMinLength();

			// TODO : are these the only set of converters?
			if (patternConverter instanceof DatePatternConverter) {

				format = getTimeStampFormat(logPatternStr);

				format = format.replaceAll(Pattern.quote("+"), "[+]");

				format = format.replaceAll(("[" + DateTimeUtilities.VALID_DATEFORMAT_CHARS + "]+"), "\\\\S+");

				format = format.replaceAll(Pattern.quote("."), "\\\\.");

				format = "(" + format + ")";

				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.TIMESTAMP.getColumnId());

				// column list has additional LINE, DELTA column
				timestampIndex = logEntryColumnList.size() - 3;

			} else if (patternConverter instanceof MessagePatternConverter) {
				format = GREEDY_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.MESSAGE.getColumnId());
				// column list has additional LINE, DELTA column
				messageIndex = logEntryColumnList.size() - 3;
			} else if (patternConverter instanceof LoggerPatternConverter) {
				format = NOSPACE_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.LOGGER.getColumnId());
				// column list has additional LINE, DELTA column
				loggerIndex = logEntryColumnList.size() - 3;
			} else if (patternConverter instanceof ClassNamePatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.CLASS.getColumnId());
			} else if (patternConverter instanceof RelativeTimePatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.RELATIVETIME.getColumnId());
			} else if (patternConverter instanceof ThreadNamePatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.THREAD.getColumnId());
			} else if (patternConverter instanceof NdcPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.NDC.getColumnId());
			} else if (patternConverter instanceof LiteralPatternConverter) {
				format = ((LiteralPatternConverter) patternConverter).getLiteral();
				// format = format.replaceAll(Pattern.quote("*"), ".*?");
				format = escapeRegexChars(format);
				// format = format.replaceAll(MULTIPLE_SPACES_REGEXP,
				// MULTIPLE_SPACES_REGEXP);
				regExp = regExp + format;
			} else if (patternConverter instanceof SequenceNumberPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.LOG4JID.getColumnId());
			} else if (patternConverter instanceof LevelPatternConverter) {

				if (minLength > 0) {
					format = LIMITING_GROUP;
					// the Regex parsing is very slow if the max value is open
					// ended like {5,} hence setting both min and max to min
					// value because i assume that standard log levels are
					// not going to be greater than 5 chars.
					format = format.replace("n", String.valueOf(minLength));
					format = format.replace("m", String.valueOf(minLength));

				} else {
					format = NOSPACE_GROUP;
				}

				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.LEVEL.getColumnId());
				// column list has additional LINE, DELTA column
				levelIndex = logEntryColumnList.size() - 3;
			} else if (patternConverter instanceof MethodLocationPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.METHOD.getColumnId());
			} else if (patternConverter instanceof FullLocationPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.LOCATIONINFO.getColumnId());
			} else if (patternConverter instanceof LineLocationPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.LINE.getColumnId());
			} else if (patternConverter instanceof FileLocationPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				logEntryColumnList.add(LogEntryColumn.FILE.getColumnId());
			} else if (patternConverter instanceof MdcPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				String patternConverterName = patternConverter.getName();
				String propertyName = getMDCName(patternConverterName);
				logEntryColumnList.add(propertyName.toUpperCase());
			} else if (patternConverter instanceof MapPatternConverter) {
				format = DEFAULT_GROUP;
				regExp = regExp + format;
				String patternConverterName = patternConverter.getName();
				String propertyName = getMAPName(patternConverterName);
				logEntryColumnList.add(propertyName.toUpperCase());
			} else if (patternConverter instanceof LineSeparatorPatternConverter) {
				lineCount++;
			}

		}

		// LOG.info("loggerIndex: " + levelIndex);
		LOG.info("generateRegExp - lineCount: " + lineCount);
		LOG.info("generateRegExp - regExp: " + regExp);
		LOG.info("generateRegExp - logEntryColumnList: " + logEntryColumnList);

		linePattern = Pattern.compile(regExp);
	}

	private static String getMDCName(String patternConverterName) {

		String propertyName = patternConverterName;
		Pattern pattern = Pattern.compile("MDC\\{(.*?)\\}");

		Matcher matcher = pattern.matcher(patternConverterName);

		boolean found = matcher.find();

		if (found) {
			propertyName = matcher.group(1).trim();
		}

		return propertyName;
	}

	private static String getMAPName(String patternConverterName) {

		String propertyName = patternConverterName;
		Pattern pattern = Pattern.compile("MAP\\{(.*?)\\}");

		Matcher matcher = pattern.matcher(patternConverterName);

		boolean found = matcher.find();

		if (found) {
			propertyName = matcher.group(1).trim();
		}

		return propertyName;
	}

	private static String getPropertyName(String log4jPattern, int counter) {

		String propertyName = null;

		Pattern pattern = Pattern.compile("X\\{(.*?)\\}");

		Matcher matcher = pattern.matcher(log4jPattern);

		// counter is 0 based.
		int index = 0;
		while (matcher.find()) {

			if (index == counter) {
				propertyName = matcher.group(1);
			}
			index++;
		}

		return propertyName;
	}

	@Override
	public void parse(String line) {

		if (parseLineCount < lineCount) {
			// accumulate
			parseLine = parseLine + System.getProperty("line.separator") + line;
			parseLineCount++;

			return;
		} else {
			if (parseLineCount > 1) {
				parseLine = parseLine + System.getProperty("line.separator") + line;
			} else {
				parseLine = line;
			}
		}

		Matcher lineMatcher = linePattern.matcher(parseLine);
		// capture the current one and build the previous one
		if (lineMatcher.matches()) {

			buildLogEntry();

			logEntryText = parseLine;

			MatchResult result = lineMatcher.toMatchResult();

			for (int i = 1; i < result.groupCount() + 1; i++) {
				// String key = logEntryColumnList.get(i - 1);
				String value = result.group(i);

				logEntryColumnValueList.add(value);
			}

		} else {
			additionalLines.add(parseLine);
		}

	}

	// construct the last event when the are no more log lines
	@Override
	public void parseFinal() {

		buildLogEntry();

		resetProcessedCount();
	}

	@Override
	public LogEntryModel getLogEntryModel() {
		return log4jLogEntryModel;
	}

	private void buildLogEntry() {

		if ((logEntryColumnValueList.size() == 0) && (additionalLines.size() > 0)) {

			LOG.info("found " + additionalLines.size() + " additional lines at the begining.");

			logEntryIndex = logEntryIndex + additionalLines.size();

			additionalLines.clear();
		} else if (logEntryColumnValueList.size() > 0) {

			logEntryIndex = logEntryIndex + 1;

			boolean sysdateEntry = false;
			boolean systemStartEntry = false;
			boolean threadDumpEntry = false;
			boolean requestorLockEntry = false;
			boolean exceptionsEntry = false;

			byte logLevelId = 0;

			LogEntryModel logEntryModel = getLogEntryModel();

			String logger = logEntryColumnValueList.get(loggerIndex).toUpperCase();

			String message = logEntryColumnValueList.get(messageIndex);
			message = message.trim();

			// TODO: on some systems like GC, there are multiple
			// spaces before '%m%n' pattern, for ex ' - %m%n'. this causes
			// the system string to have a additional space in the front hence
			// it is required to trim the message to match the entries below

			if ((logger.endsWith("INTERNAL.ASYNC.AGENT")) || (logger.endsWith("ENGINE.CONTEXT.AGENT"))) {

				if (message.startsWith("System date:")) {

					sysdateEntry = true;

					if (modelTimezone == null) {

						Matcher systemDateMatcher = systemDatepattern.matcher(message);

						if (systemDateMatcher.matches()) {

							String timezoneID = systemDateMatcher.group(1).trim();
							LOG.info("Identified System Date timezoneID: " + timezoneID);

							modelTimezone = TimeZoneUtil.getTimeZoneFromAbbreviatedString(timezoneID);

							if (modelTimezone == null) {

								TimeZone currTimezone = logEntryModel.getModelDateFormat().getTimeZone();

								LOG.error("Unable to detect timezone, using " + currTimezone,
										new Exception("Unable to parse timezoneID: " + timezoneID));

								modelTimezone = TimeZoneUtil.getTimeZoneFromAbbreviatedString("GMT");

							} else {
								LOG.info("Setting modelTimezone: " + modelTimezone);
							}

							logEntryModel.setModelDateFormatTimeZone(modelTimezone);

							if (displayTimezone == null) {
								logEntryModel.setDisplayDateFormatTimeZone(modelTimezone);
								LOG.info("Setting displayTimezone: " + displayTimezone);
							}
						} else {
							LOG.info("attempt to extract system date modelTimezone failed");
						}
					}
				}
			} else if (logger.endsWith("ERVLET.WEBAPPLIFECYCLELISTENER")) {

				if ((systemStart == null) && (message.startsWith("System Start Date"))) {

					systemStartEntry = true;

				} else if ((systemStart != null) && (message.startsWith("System Start Date"))) {
					// may end up here when jvm is killed and restarted.
					LOG.info("Found a killed jvm and restart");

					Integer systemStartEndIndex = new Integer(logEntryIndex - 1);
					systemStart.setEndIndex(systemStartEndIndex);
					systemStart.setAbruptStop(true);
					
					systemStartEntry = true;
					systemStart = null;

				} else if (systemStart != null) {

					if ((message.startsWith("Web Tier initialization is complete."))
							|| (message.startsWith("Exception during startup processing"))
							|| (message.startsWith("Thin Web Tier initialization is complete."))) {

						Integer systemStartEndIndex = new Integer(logEntryIndex);
						systemStart.setEndIndex(systemStartEndIndex);

						// this systemStart is already added to the list.
						systemStart = null;
					}
				}
			} else if (logger.endsWith(".ENVIRONMENTDIAGNOSTICS")) {

				if (message.startsWith("--- Thread Dump Starts ---")) {

					threadDumpEntry = true;

					List<Integer> threadDumpLogEntryIndexList;
					threadDumpLogEntryIndexList = log4jLogEntryModel.getThreadDumpLogEntryIndexList();
					threadDumpLogEntryIndexList.add(logEntryIndex);

				}
			}

			String level = logEntryColumnValueList.get(levelIndex);
			logLevelId = getLogLevelId(level.trim());

			// check only first 3 lines of the additional line set.
			int exceptionLineCounter = 0;

			// construct log entry text
			StringBuffer fullLogEntryTextSB = new StringBuffer();
			fullLogEntryTextSB.append(logEntryText);

			if (additionalLines.size() > 0) {

				for (String line : additionalLines) {

					fullLogEntryTextSB.append("\n");
					fullLogEntryTextSB.append(line);

					Map<String, List<Integer>> errorLogEntryIndexMap = log4jLogEntryModel.getErrorLogEntryIndexMap();

					// in case of error log check if there is an exception in
					// the lines
					if ((logLevelId > 5) && (exceptionLineCounter < 3) && (!exceptionsEntry)) {

						Matcher matcher = exceptionPattern.matcher(line);

						if (matcher.find()) {

							String exception = matcher.group(0);

							List<Integer> errorLogEntryIndexList;
							errorLogEntryIndexList = errorLogEntryIndexMap.get(exception);

							if (errorLogEntryIndexList == null) {
								errorLogEntryIndexList = new ArrayList<Integer>();
								errorLogEntryIndexMap.put(exception, errorLogEntryIndexList);
							}

							errorLogEntryIndexList.add(logEntryIndex);
							exceptionsEntry = true;

						}
						// if the exception is not found in the first 3
						// additional
						// lines, then search in the main text.
					}

					exceptionLineCounter++;
				}
			}

			// if this is just an error entry, capture it in an empty group
			if ((logLevelId > 5) && (!exceptionsEntry)) {

				Map<String, List<Integer>> errorLogEntryIndexMap = log4jLogEntryModel.getErrorLogEntryIndexMap();

				Matcher matcher = exceptionPattern.matcher(message);

				if (matcher.find()) {

					String exception = matcher.group(0);

					List<Integer> errorLogEntryIndexList;
					errorLogEntryIndexList = errorLogEntryIndexMap.get(exception);

					if (errorLogEntryIndexList == null) {
						errorLogEntryIndexList = new ArrayList<Integer>();
						errorLogEntryIndexMap.put(exception, errorLogEntryIndexList);
					}

					errorLogEntryIndexList.add(logEntryIndex);
				} else {
					List<Integer> errorLogEntryIndexList;
					errorLogEntryIndexList = errorLogEntryIndexMap.get("");

					if (errorLogEntryIndexList == null) {
						errorLogEntryIndexList = new ArrayList<Integer>();
						errorLogEntryIndexMap.put("", errorLogEntryIndexList);
					}

					errorLogEntryIndexList.add(logEntryIndex);
				}

				exceptionsEntry = true;

			}

			String fullLogEntryText = fullLogEntryTextSB.toString();

			Log4jLogEntry log4jLogEntry;
			String deltaStr = null;

			String timestampStr = logEntryColumnValueList.get(timestampIndex);

			DateFormat modelDateFormat = logEntryModel.getModelDateFormat();

			Date logEntryDate = null;
			try {

				logEntryDate = modelDateFormat.parse(timestampStr);

				if (prevLogEntryDate != null) {

					long logEntryTime = logEntryDate.getTime();
					long prevTime = prevLogEntryDate.getTime();

					long delta = logEntryTime - prevTime;

					deltaStr = Long.toString(delta);
				}

				prevLogEntryDate = logEntryDate;
			} catch (ParseException e) {
				LOG.error("Error parsing line: [" + logEntryIndex + "] logentry: [" + logEntryText + "]", e);
			}

			// if a thread dump is found earlier then look for 'Unable to
			// synchronize' messages. also need to check that whether this
			// message is within 5 minutes (using default setting) of the
			// occurrence of thread dump

			if ((!threadDumpEntry) && (log4jLogThreadDumpEntry != null) && (logEntryDate != null)) {

				// check if we are within 5 minutes threshold
				long threadDumpEntryLogEntryTime = log4jLogThreadDumpEntry.getLogEntryDate().getTime();

				long logEntryTime = logEntryDate.getTime();

				threadDumpEntryLogEntryTime = threadDumpEntryLogEntryTime + THREADDUMP_TIMESTAMP_INTERVAL;

				if ((threadDumpEntryLogEntryTime > logEntryTime)) {

					Matcher discardedMessagesPatternMatcher = discardedMessagesPattern.matcher(message);
					boolean discardedLogMessage = discardedMessagesPatternMatcher.matches();

					int rleIndex = message.indexOf("com.pega.pegarules.pub.context.RequestorLockException");

					if ((!discardedLogMessage) && (rleIndex != -1)) {

						// also check the message "Unable to synchronize on
						// requestor"
						String syncMessage = additionalLines.get(0);

						int smIndex = syncMessage.indexOf("Unable to synchronize on requestor");

						if (smIndex != -1) {
							requestorLockEntry = true;
						}
					}
				} else {
					// we have passed the 5 minutes threshold. thread dump
					// entry is not required now.
					log4jLogThreadDumpEntry = null;
				}
			}

			// add all last otherwise messes up the indexes
			logEntryColumnValueList.add(0, String.valueOf(logEntryIndex));
			logEntryColumnValueList.add(1, deltaStr);

			if (threadDumpEntry) {
				log4jLogThreadDumpEntry = new Log4jLogThreadDumpEntry(logEntryIndex, logEntryDate,
						logEntryColumnValueList, fullLogEntryText, sysdateEntry, logLevelId);

				log4jLogEntry = log4jLogThreadDumpEntry;

			} else if (requestorLockEntry) {

				Log4jLogRequestorLockEntry log4jLogRequestorLockEntry;

				log4jLogRequestorLockEntry = new Log4jLogRequestorLockEntry(logEntryIndex, logEntryDate,
						logEntryColumnValueList, fullLogEntryText, sysdateEntry, logLevelId);

				String requestorLockStr = additionalLines.get(0);

				parseRequestorLockString(requestorLockStr, log4jLogRequestorLockEntry);

				List<Log4jLogRequestorLockEntry> log4jLogRequestorLockEntryList;

				log4jLogRequestorLockEntryList = log4jLogThreadDumpEntry.getLog4jLogRequestorLockEntryList();

				log4jLogRequestorLockEntryList.add(log4jLogRequestorLockEntry);

				log4jLogEntry = log4jLogRequestorLockEntry;

			} else if (systemStartEntry) {
				log4jLogEntry = new Log4jLogSystemStartEntry(logEntryIndex, logEntryDate, logEntryColumnValueList,
						fullLogEntryText, sysdateEntry, logLevelId);
			} else if (exceptionsEntry) {
				log4jLogEntry = new Log4jLogExceptionEntry(logEntryIndex, logEntryDate, logEntryColumnValueList,
						fullLogEntryText, sysdateEntry, logLevelId);
			} else {
				log4jLogEntry = new Log4jLogEntry(logEntryIndex, logEntryDate, logEntryColumnValueList,
						fullLogEntryText, sysdateEntry, logLevelId);
			}

			// Log4jLogEntry log4jLogEntry = new Log4jLogEntry(logEntryIndex,
			// logEntryColumnValueList, fullLogEntryText, sysdateEntry,
			// systemStartEntry, threadDumpEntry, levelId);

			if (systemStartEntry) {

				DateFormat displayDateFormat = log4jLogEntryModel.getDisplayDateFormat();

				systemStart = new SystemStart(systemStartCounter, displayDateFormat, log4jLogEntry);

				List<SystemStart> systemStartList;
				systemStartList = log4jLogEntryModel.getSystemStartList();

				systemStartList.add(systemStart);
				systemStartCounter++;

			} else if (systemStart != null) {
				Integer systemStartEndIndex = new Integer(logEntryIndex);
				systemStart.setEndIndex(systemStartEndIndex);
			}

			logEntryIndex = logEntryIndex + additionalLines.size();

			log4jLogEntryModel.addLogEntry(log4jLogEntry, logEntryColumnValueList);

			// update the processed counter
			incrementAndGetProcessedCount();

			logEntryText = null;
			logEntryColumnValueList.clear();
			additionalLines.clear();
		}

	}

	private byte getLogLevelId(String level) {

		byte levelId = 0;

		if (level.equalsIgnoreCase("TRACE")) {
			levelId = 1;
		} else if (level.equalsIgnoreCase("DEBUG")) {
			levelId = 2;
		} else if (level.equalsIgnoreCase("INFO")) {
			levelId = 3;
		} else if (level.equalsIgnoreCase("WARN")) {
			levelId = 4;
		} else if (level.equalsIgnoreCase("ALERT")) {
			levelId = 5;
		} else if (level.equalsIgnoreCase("ERROR")) {
			levelId = 6;
		} else if (level.equalsIgnoreCase("FATAL")) {
			levelId = 7;
		}

		return levelId;

	}

	private void parseRequestorLockString(String requestorLockStr,
			Log4jLogRequestorLockEntry log4jLogRequestorLockEntry) {

		Matcher lineMatcher = requestorLockPattern.matcher(requestorLockStr);

		if (lineMatcher.matches()) {

			int count = lineMatcher.groupCount();

			if (count >= 6) {

				String requestorId = null;
				Integer timeInterval = null;
				String thisThreadName = null;
				String originalLockThreadName = null;
				String finallyLockThreadName = null;

				requestorId = lineMatcher.group(2);
				String timeIntervalStr = lineMatcher.group(3);

				try {
					timeInterval = Integer.parseInt(timeIntervalStr);
				} catch (Exception e) {
					LOG.error("Error parsing time interval: " + timeIntervalStr, e);
				}

				thisThreadName = lineMatcher.group(4);
				originalLockThreadName = lineMatcher.group(5);
				finallyLockThreadName = lineMatcher.group(6);

				log4jLogRequestorLockEntry.setRequestorId(requestorId);
				log4jLogRequestorLockEntry.setTimeInterval(timeInterval);
				log4jLogRequestorLockEntry.setThisThreadName(thisThreadName);
				log4jLogRequestorLockEntry.setOriginalLockThreadName(originalLockThreadName);
				log4jLogRequestorLockEntry.setFinallyLockThreadName(finallyLockThreadName);
			}

		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> logLineList = new ArrayList<>();

		logLineList.add(
				"2017-05-04 22:39:49,345 [orkmanager_PRPC : 12] [  STANDARD] [     PegaRULES:07.10] (al.authorization.Authorization) DEBUG File.RWAOperatorListener|process|A3E07D4B2E67913EABBDC14E695AA7EC6|1493930054694000|RWAOperatorsPackage/RBG-Int-RWAOperators-/ProcessRWAOperators batchprocess@rabobank.com - pxReqServletName = /xvVsgsdbXM6xHWMgWNTRXVYCwM--oh4KDYhMzQmFsNBszM62iOq0-w%5B%5B*/!STANDARD");

		logLineList.add(
				"2017-05-04 22:39:49,350 [orkmanager_PRPC : 12] [  STANDARD] [     PegaRULES:07.10] (.ManagedApplicationContextImpl) INFO  File.RWAOperatorListener|process|A3E07D4B2E67913EABBDC14E695AA7EC6|1493930054694000|RWAOperatorsPackage/RBG-Int-RWAOperators-/ProcessRWAOperators batchprocess@rabobank.com - Invalid configuration: application context defined by access Group(RBGRaboCollectionsImpl:Administrators) and Application:Version(RBGRaboCollectionsCurrent:01.01.01) defines one or more ruleset that override ruleset(s) in ancestor PegaRULES:[Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-DecisionArchitect:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-DecisionEngine:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]\"; RuleSetList=[PrivateSale_nl:01-19, ServiceCustomer_nl:03-19, WorkInstructions_nl:01-16, RaboCollectionsFW_nl:03-21, RBGBusinessViews_nl:03-18, RBGSendLetter_Refactor_nl:03-02, RBGInt_nl:03-21, RBG_nl:03-21, Pega-ProcessCommander_nl:07-10, Pega-LP-ProcessAndRules_nl:07-10, Pega-LP-SystemSettings_nl:07-10, Pega-LP-UserInterface_nl:07-10, Pega-LP-OrgAndSecurity_nl:07-10, Pega-LP-DataModel_nl:07-10, Pega-LP-Application_nl:07-10, Pega-LP_nl:07-10, Pega-UpdateManager_nl:07-10, Pega-Feedback_nl:07-10, Pega-AutoTest_nl:07-10, Pega-AppDefinition_nl:07-10, Pega-ImportExport_nl:07-10, Pega-LocalizationTools_nl:07-10, Pega-RuleRefactoring_nl:07-10, Pega-ProcessArchitect_nl:07-10, Pega-IntegrationArchitect_nl:07-10, Pega-SystemArchitect_nl:07-10, Pega-Desktop_nl:07-10, Pega-EndUserUI_nl:07-10, Pega-Reporting_nl:07-10, Pega-UIDesign_nl:07-10, Pega-Gadgets_nl:07-10, Pega-ProcessEngine_nl:07-10, Pega-SearchEngine_nl:07-10, Pega-IntegrationEngine_nl:07-10, Pega-RulesEngine_nl:07-10, Pega-Engine_nl:07-10, Pega-ProCom_nl:07-10, Pega-IntSvcs_nl:07-10, Pega-WB_nl:07-10, Pega-RULES_nl:07-10, RBGRaboCollectionsCurrent:03-21, RBGRaboImpl:03-21, RBGMigrationScripts:03-02, Reminder:01-18, PrivateSale:01-19, Hospital:01-18, FallBackDossier:01-17, PaymentArrangement:01-19, ServiceCustomer:03-19, WorkInstructions:01-16, RaboCollectionsFW:03-21, RBGBusinessViews:03-18, RBGBusServPopulateCommObject:03-01, RBGBusServPreviewLetters:03-01, RBGSendLetter_Refactor:03-02, RBGIntPrintNet:03-19, SAMportal:03-18, BulkTransfer:03-18, RBGBusinessServices:03-21, RBGBusServGetFacilities:03-21, RBGBusServArchiveDocumentEKD:03-01, RBGBusServGetRelations:03-01, RBGBusServGetRelationsV2:03-01, RBGBusServGetDocumentEKD:03-01, RBGBusServGetEmployeeDetails:03-01, RBGBusServGetProductArrangementList:03-01, RBGBusServGetCollateralObject:03-16, RBGBusServGetCollateralArrangement:03-18, RBGBusServGetRelation:03-01, RBGBusServSendNotification:03-01, RBGBusServSendDefaultSignal:03-21, RBGBusServGetProductArrangement:03-17, RBGInt:03-21, RBGIntPrintNetFW:03-01, RBGRelationArrangementViewCRMI20Int:03-01, RBGArchiveDocumentInt:03-01, RBGGetRelationDocumentInt:03-02, RBGGetRelationCRMI19Int:03-02, RBGSearchCustomerCRMI18Int:03-01, RBGIntCPS69RaadplegenInstelling:03-18, RBGIntCPS31RaadplegenRegister:03-01, RBGIntCPS63SelecterenVerpanding:03-01, RBGIntCPS66SetBBInd:03-01, RBGIntCPS64SelecterenBorgtocht:03-01, RBGIntCPS62SelecterenHypotheek:03-01, RBGIntCPSArrears:03-18, RBGIntCPS32GetLoanDetails:03-02, RBGIntGloba:03-01, RBGGetDossierForce7Int:03-01, RBGGetFinancialDossierSAP6Int:03-02, RBGIntSAPArrears:03-02, RBGIntSAPLetters:03-01, RBGRWAOperators:03-20, RBGRWACaseBasedSecurity:03-02, RBGRWASSOInt:03-01, RBGGetSavingDepositDetailsAZS21Int:03-01, RBGIntCRMi59GetIDFromCrabNumber:03-01, RBGIntCRMi61GetArrangement:03-01, RBGIntCRMi67UpdateServiceRequest:03-01, RBGIntCRMi29CreateServiceRequest:03-01, RBGIntEKD28GetRelationDocument:03-01, RBGIntEKD42ArchiveDocument:03-01, RBGIntGetFacilities:03-21, RBGIntBatchStatus:03-02, RBGIntArrearsBatch:03-01, RBGIntCache:03-02, RBGIntBatch:03-01, RBGIntRWAFW:03-01, RBGIntGlobaFW:01-01, RBGIntEKDFW:03-01, RBGIntCRMiFW:03-01, RBGIntCPSFW:03-02, RBGIntFLFFW:03-21, RBGIntBatchFW:03-02, RBGIntAZSFW:01-01, RBGIntFW:03-03, InnovationCobol:03-01, RBGIntDSPFW:03-21, RBGIntDSP:03-21, RBGRabo:03-21, RBG:03-21, RBGArchivingUniqueId:03-19, RBGUtilities:03-17, DynamicClassReferencing:03-01, CommonFunctionLibrary:03-02, RBGDataModel:03-21, UI-Kit-7:03-01, SamSenses:01-18, PegaCPMFS:07-14, PegaCPMFSInt:07-14, PegaFSSCM:07-14, PegaNPVCalc:07-14, PegaLoanPmtCalc:07-14, Pega-DecisionArchitect:07-10, Pega-DecisionEngine:07-10, CPM:07-14, CPM-Social:07-14, CPM-Reports:07-14, PegaAppCA:07-14, PegaFW-Social:07-14, PegaKM:07-14, KMReports:07-14, PegaFW-NewsFeed:07-14, Pega-LP_CPM:07-14, PegaApp:07-14, PegaFW-Chat:07-14, Pega-Chat:07-14, Pega-CTI:07-13, Pega-ChannelServices:07-13, Pega-UITheme:07-14, PegaFSRequirements:07-15, PegaFS:07-15, PegaFSInt:07-15, PegaAccounting:07-15, PegaAccounting-Classes:07-15, PegaEQ:07-15, PegaEQInt:07-15, Pega-IAC:07-10, PegaRequirements:07-15, CMISPlus:07-15, PegaFSUI:07-15, PegaFWUI:07-13, PegaSCM:07-14, PegaSCMInt:07-14, Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]");

		logLineList.add(
				"2017-04-26 10:58:07,489 [    EMAIL-Thread-266] [  STANDARD] [         MWDSS:03.01] (yMails.MW_FW_UAPFW_Work.Action) INFO  EMAIL.DSSCSRListener.Listener|from(haripriyas@incessanttechnologies.com)|sub(New triage)|Email|DSSEmailDefault|MW-SD-DSS-WORK-Application|ProcessDSSEnquiryMails|A361D4FF08DDD162D9C290282C9629AC3 DSSBatchProcess -  WorkObject hit the devconTime:20170426T105807.489 GMT");

		logLineList.add(
				"2017-05-04 21:56:11,103 [workmanager_PRPC : 2] [  STANDARD] [     PegaRULES:07.10] (.ManagedApplicationContextImpl) INFO    - Invalid configuration: application context defined by access Group(RBGRaboCollectionsImpl:Administrators) and Application:Version(RBGRaboCollectionsCurrent:01.01.01) defines one or more ruleset that override ruleset(s) in ancestor PegaRULES:[Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-DecisionArchitect:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-DecisionEngine:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]\"; RuleSetList=[PrivateSale_nl:01-19, ServiceCustomer_nl:03-19, WorkInstructions_nl:01-16, RaboCollectionsFW_nl:03-21, RBGBusinessViews_nl:03-18, RBGSendLetter_Refactor_nl:03-02, RBGInt_nl:03-21, RBG_nl:03-21, Pega-ProcessCommander_nl:07-10, Pega-LP-ProcessAndRules_nl:07-10, Pega-LP-SystemSettings_nl:07-10, Pega-LP-UserInterface_nl:07-10, Pega-LP-OrgAndSecurity_nl:07-10, Pega-LP-DataModel_nl:07-10, Pega-LP-Application_nl:07-10, Pega-LP_nl:07-10, Pega-UpdateManager_nl:07-10, Pega-Feedback_nl:07-10, Pega-AutoTest_nl:07-10, Pega-AppDefinition_nl:07-10, Pega-ImportExport_nl:07-10, Pega-LocalizationTools_nl:07-10, Pega-RuleRefactoring_nl:07-10, Pega-ProcessArchitect_nl:07-10, Pega-IntegrationArchitect_nl:07-10, Pega-SystemArchitect_nl:07-10, Pega-Desktop_nl:07-10, Pega-EndUserUI_nl:07-10, Pega-Reporting_nl:07-10, Pega-UIDesign_nl:07-10, Pega-Gadgets_nl:07-10, Pega-ProcessEngine_nl:07-10, Pega-SearchEngine_nl:07-10, Pega-IntegrationEngine_nl:07-10, Pega-RulesEngine_nl:07-10, Pega-Engine_nl:07-10, Pega-ProCom_nl:07-10, Pega-IntSvcs_nl:07-10, Pega-WB_nl:07-10, Pega-RULES_nl:07-10, Hospital_Branch_Jan-Jaap:, RBGRaboCollectionsCurrent:03-21, RBGRaboImpl:03-21, RBGMigrationScripts:03-02, Reminder:01-18, PrivateSale:01-19, Hospital:01-18, FallBackDossier:01-17, PaymentArrangement:01-19, ServiceCustomer:03-19, WorkInstructions:01-16, RaboCollectionsFW:03-21, RBGBusinessViews:03-18, RBGBusServPopulateCommObject:03-01, RBGBusServPreviewLetters:03-01, RBGSendLetter_Refactor:03-02, RBGIntPrintNet:03-19, SAMportal:03-18, BulkTransfer:03-18, RBGBusinessServices:03-21, RBGBusServGetFacilities:03-21, RBGBusServArchiveDocumentEKD:03-01, RBGBusServGetRelations:03-01, RBGBusServGetRelationsV2:03-01, RBGBusServGetDocumentEKD:03-01, RBGBusServGetEmployeeDetails:03-01, RBGBusServGetProductArrangementList:03-01, RBGBusServGetCollateralObject:03-16, RBGBusServGetCollateralArrangement:03-18, RBGBusServGetRelation:03-01, RBGBusServSendNotification:03-01, RBGBusServSendDefaultSignal:03-21, RBGBusServGetProductArrangement:03-17, RBGInt:03-21, RBGIntPrintNetFW:03-01, RBGRelationArrangementViewCRMI20Int:03-01, RBGArchiveDocumentInt:03-01, RBGGetRelationDocumentInt:03-02, RBGGetRelationCRMI19Int:03-02, RBGSearchCustomerCRMI18Int:03-01, RBGIntCPS69RaadplegenInstelling:03-18, RBGIntCPS31RaadplegenRegister:03-01, RBGIntCPS63SelecterenVerpanding:03-01, RBGIntCPS66SetBBInd:03-01, RBGIntCPS64SelecterenBorgtocht:03-01, RBGIntCPS62SelecterenHypotheek:03-01, RBGIntCPSArrears:03-18, RBGIntCPS32GetLoanDetails:03-02, RBGIntGloba:03-01, RBGGetDossierForce7Int:03-01, RBGGetFinancialDossierSAP6Int:03-02, RBGIntSAPArrears:03-02, RBGIntSAPLetters:03-01, RBGRWAOperators:03-20, RBGRWACaseBasedSecurity:03-02, RBGRWASSOInt:03-01, RBGGetSavingDepositDetailsAZS21Int:03-01, RBGIntCRMi59GetIDFromCrabNumber:03-01, RBGIntCRMi61GetArrangement:03-01, RBGIntCRMi67UpdateServiceRequest:03-01, RBGIntCRMi29CreateServiceRequest:03-01, RBGIntEKD28GetRelationDocument:03-01, RBGIntEKD42ArchiveDocument:03-01, RBGIntGetFacilities:03-21, RBGIntBatchStatus:03-02, RBGIntArrearsBatch:03-01, RBGIntCache:03-02, RBGIntBatch:03-01, RBGIntRWAFW:03-01, RBGIntGlobaFW:01-01, RBGIntEKDFW:03-01, RBGIntCRMiFW:03-01, RBGIntCPSFW:03-02, RBGIntFLFFW:03-21, RBGIntBatchFW:03-02, RBGIntAZSFW:01-01, RBGIntFW:03-03, InnovationCobol:03-01, RBGIntDSPFW:03-21, RBGIntDSP:03-21, RBGRabo:03-21, RBG:03-21, RBGArchivingUniqueId:03-19, RBGUtilities:03-17, DynamicClassReferencing:03-01, CommonFunctionLibrary:03-02, RBGDataModel:03-21, UI-Kit-7:03-01, SamSenses:01-18, PegaCPMFS:07-14, PegaCPMFSInt:07-14, PegaFSSCM:07-14, PegaNPVCalc:07-14, PegaLoanPmtCalc:07-14, CPM:07-14, CPM-Social:07-14, CPM-Reports:07-14, PegaAppCA:07-14, PegaFW-Social:07-14, PegaKM:07-14, KMReports:07-14, PegaFW-NewsFeed:07-14, PegaApp:07-14, PegaFW-Chat:07-14, PegaFSRequirements:07-15, PegaFS:07-15, PegaFSInt:07-15, PegaAccounting:07-15, PegaAccounting-Classes:07-15, PegaEQ:07-15, PegaEQInt:07-15, PegaRequirements:07-15, CMISPlus:07-15, PegaFSUI:07-15, PegaFWUI:07-13, PegaSCM:07-14, PegaSCMInt:07-14, Pega-DecisionArchitect:07-10, Pega-DecisionEngine:07-10, Pega-LP_CPM:07-14, Pega-Chat:07-14, Pega-CTI:07-13, Pega-ChannelServices:07-13, Pega-UITheme:07-14, Pega-IAC:07-10, Pega-ProcessCommander:07-10, Pega-DeploymentDefaults:07-10, Pega-LP-Mobile:07-10, Pega-LP-ProcessAndRules:07-10, Pega-LP-Integration:07-10, Pega-LP-Reports:07-10, Pega-LP-SystemSettings:07-10, Pega-LP-UserInterface:07-10, Pega-LP-OrgAndSecurity:07-10, Pega-LP-DataModel:07-10, Pega-LP-Application:07-10, Pega-LP:07-10, Pega-UpdateManager:07-10, Pega-SecurityVA:07-10, Pega-Feedback:07-10, Pega-AutoTest:07-10, Pega-AppDefinition:07-10, Pega-ImportExport:07-10, Pega-LocalizationTools:07-10, Pega-RuleRefactoring:07-10, Pega-ProcessArchitect:07-10, Pega-Portlet:07-10, Pega-Content:07-10, Pega-BigData:07-10, Pega-NLP:07-10, Pega-IntegrationArchitect:07-10, Pega-SystemArchitect:07-10, Pega-Desktop:07-10, Pega-EndUserUI:07-10, Pega-Social:07-10, Pega-API:07-10, Pega-EventProcessing:07-10, Pega-Reporting:07-10, Pega-UIDesign:07-10, Pega-Gadgets:07-10, Pega-UIEngine:07-10, Pega-ProcessEngine:07-10, Pega-SearchEngine:07-10, Pega-IntegrationEngine:07-10, Pega-RulesEngine:07-10, Pega-Engine:07-10, Pega-ProCom:07-10, Pega-IntSvcs:07-10, Pega-WB:07-10, Pega-RULES:07-10]");
		// String regex = "(\\S+-\\S+-\\S+
		// \\S+:\\S+:\\S+,\\S+)\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\((\\s*?\\S*?\\s*?)\\)\\s(.{5,5})\\s(.*?)\\s(.*?)\\s\\-\\s(.*)";
		String regex = "(\\S+-\\S+-\\S+ \\S+:\\S+:\\S+,\\S+)\\s\\[(.*?)\\]\\s\\[(.*)\\]\\s\\[(.*)\\]\\s\\((\\s*?\\S*?\\s*?)\\)\\s(.{5,5})\\s(.*)\\s(.*)\\s\\-\\s(.*)";
		// String regex = "(\\S+-\\S+-\\S+
		// \\S+:\\S+:\\S+,\\S+)\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\[(.*?)\\]\\s\\((\\s*?\\S*?\\s*?)\\)\\s(\\s*?\\S*?\\s*?)\\s(.*?)\\s(.*?)\\s\\-\\s(.*)";
		Pattern pattern = Pattern.compile(regex);

		for (String logLine : logLineList) {
			Matcher matcher = pattern.matcher(logLine);
			if (matcher.matches()) {
				System.out.println("Match");
				for (int i = 1; i <= matcher.groupCount(); i++) {
					System.out.println("Group " + i + ". " + matcher.group(i));
				}
			} else {
				System.out.println("No Match");
			}
		}
	}
}
