/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.logviewer.logfile.LogFileType;
import com.pega.gcs.logviewer.logfile.LogFileType.LogType;
import com.pega.gcs.logviewer.logfile.LogPattern;
import com.pega.gcs.logviewer.model.LogEntryModel;

public abstract class LogParser {

	private static final Log4j2Helper LOG = new Log4j2Helper(LogParser.class);

	private LogFileType logFileType;

	private DateFormat dateFormat;

	private Locale locale;

	public abstract void parse(String line);

	public abstract void parseFinal();

	public abstract LogEntryModel getLogEntryModel();

	private AtomicInteger processedCount;

	private boolean resetProcessedCount;

	public LogParser(LogFileType logFileType, Locale locale) {
		this.dateFormat = null;
		this.logFileType = logFileType;
		this.locale = locale;
		this.processedCount = new AtomicInteger(0);
		this.resetProcessedCount = false;
	}

	public LogFileType getLogFileType() {
		return logFileType;
	}

	/**
	 * @return the dateFormat
	 */
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat
	 *            the dateFormat to set
	 */
	protected void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	protected String quoteTimeStampChars(String input) {
		// put single quotes around text that isn't a supported dateformat char
		StringBuffer result = new StringBuffer();
		// ok to default to false because we also check for index zero below
		boolean lastCharIsDateFormat = false;

		for (int i = 0; i < input.length(); i++) {

			String thisVal = input.substring(i, i + 1);
			boolean thisCharIsDateFormat = DateTimeUtilities.VALID_DATEFORMAT_CHARS.contains(thisVal);
			// we have encountered a non-dateformat char
			if (!thisCharIsDateFormat && (i == 0 || lastCharIsDateFormat)) {
				result.append("'");
			}
			// we have encountered a dateformat char after previously
			// encountering a non-dateformat char
			if (thisCharIsDateFormat && i > 0 && !lastCharIsDateFormat) {
				result.append("'");
			}
			lastCharIsDateFormat = thisCharIsDateFormat;
			result.append(thisVal);
		}
		// append an end single-quote if we ended with non-dateformat char
		if (!lastCharIsDateFormat) {
			result.append("'");
		}
		return result.toString();
	}

	protected String getTimeStampFormat(String log4jPattern) {

		String timeStampFormat = null;
		String timeZoneStr = null;

		int index = log4jPattern.indexOf("%d");

		if (index != -1) {

			index = log4jPattern.indexOf("%d{");

			// use ISO8601 format
			if (index == -1) {
				timeStampFormat = DateTimeUtilities.DATEFORMAT_ISO8601;
			} else {
				// identify format
				int formatIndex = log4jPattern.substring(index).indexOf("}");

				timeStampFormat = log4jPattern.substring(index + "%d{".length(), index + formatIndex);

				if (timeStampFormat.equals("ABSOLUTE")) {
					timeStampFormat = DateTimeUtilities.DATEFORMAT_ABSOLUTE;
				} else if ("".equals(timeStampFormat) || timeStampFormat.equals("ISO8601")) {
					timeStampFormat = DateTimeUtilities.DATEFORMAT_ISO8601;
				} else if (timeStampFormat.equals("DATE")) {
					timeStampFormat = DateTimeUtilities.DATEFORMAT_DATE;
				}

				// identify time zone-Optional
				// if the adjoining character is a curly brace
				int tzIndex = log4jPattern.substring(index + formatIndex + 1).indexOf("{");

				if (tzIndex == 0) {
					tzIndex = index + formatIndex + 1;
					timeZoneStr = log4jPattern.substring(tzIndex + "{".length(), log4jPattern.indexOf("}", tzIndex));
					timeStampFormat = timeStampFormat + " z";
				}
			}

			String quoteTimeStamp = quoteTimeStampChars(timeStampFormat);

			LOG.info("Using Timestamp format: " + quoteTimeStamp);

			DateFormat dateFormat = new SimpleDateFormat(quoteTimeStamp);

			if (timeZoneStr != null) {

				TimeZone tz = TimeZone.getTimeZone(timeZoneStr);
				dateFormat.setTimeZone(tz);
			}

			setDateFormat(dateFormat);

		}

		return timeStampFormat;
	}

	protected String escapeRegexChars(String input) {
		String escapedStr = input;

		escapedStr = escapedStr.replaceAll("\\\\", "\\\\\\");
		escapedStr = escapedStr.replaceAll(Pattern.quote("*"), "\\\\*");
		escapedStr = escapedStr.replaceAll(Pattern.quote("]"), "\\\\]");
		escapedStr = escapedStr.replaceAll(Pattern.quote("["), "\\\\[");
		escapedStr = escapedStr.replaceAll(Pattern.quote("^"), "\\\\^");
		escapedStr = escapedStr.replaceAll(Pattern.quote("$"), "\\\\$");
		escapedStr = escapedStr.replaceAll(Pattern.quote("."), "\\\\.");
		escapedStr = escapedStr.replaceAll(Pattern.quote("|"), "\\\\|");
		escapedStr = escapedStr.replaceAll(Pattern.quote("?"), "\\\\?");
		escapedStr = escapedStr.replaceAll(Pattern.quote("+"), "\\\\+");
		escapedStr = escapedStr.replaceAll(Pattern.quote("("), "\\\\(");
		escapedStr = escapedStr.replaceAll(Pattern.quote(")"), "\\\\)");
		escapedStr = escapedStr.replaceAll(Pattern.quote("-"), "\\\\-");
		escapedStr = escapedStr.replaceAll(Pattern.quote("{"), "\\\\{");
		escapedStr = escapedStr.replaceAll(Pattern.quote("}"), "\\\\}");
		escapedStr = escapedStr.replaceAll(Pattern.quote("#"), "\\\\#");
		escapedStr = escapedStr.replaceAll(Pattern.quote(" "), "\\\\s");

		return escapedStr;
	}

	public static LogParser getLogParser(String filename, List<String> readLineList, Set<LogPattern> logPatternSet,
			Locale locale, TimeZone displayTimezone) {

		LogParser logParser = null;

		// check if an Alert log file
		if (filename.toUpperCase().contains("ALERT")) {

			LogFileType logFileType = new LogFileType(LogType.PEGA_ALERT, null);

			AlertLogParser alertLogParser = new AlertLogParser(logFileType, locale);

			for (String readLine : readLineList) {
				alertLogParser.parse(readLine);
			}

			LogEntryModel lem = alertLogParser.getLogEntryModel();

			if (lem.getTotalRowCount() > 0) {
				// success
				LOG.info("creating AlertLogParser for " + filename);
				logParser = alertLogParser;
			}
		}

		// check if a PegaRules log file
		if (logParser == null) {

			for (LogPattern logPattern : logPatternSet) {

				LOG.info("Applying LogPattern: " + logPattern);

				logParser = getLogParser(readLineList, logPattern, locale, displayTimezone);

				if (logParser != null) {
					break;
				}
			}
		}

		// TODO other log file types

		return logParser;
	}

	// currently assuming all pattern as of type pegarules. change when
	// implementing other types like WAS, WLS
	public static LogParser getLogParser(List<String> readLineList, LogPattern logPattern, Locale locale,
			TimeZone displayTimezone) {

		LogParser logParser = null;

		LOG.info("Trying log Pattern " + logPattern);

		LogFileType logFileType = new LogFileType(LogType.PEGA_RULES, logPattern);

		Log4jPatternParser log4jPatternParser = new Log4jPatternParser(logFileType, locale, displayTimezone);

		for (String readLine : readLineList) {
			log4jPatternParser.parse(readLine);
		}

		log4jPatternParser.parseFinal();

		LogEntryModel lem = log4jPatternParser.getLogEntryModel();

		if (lem.getTotalRowCount() > 0) {
			// success
			LOG.info("Creating Log4jPatternParser using " + logPattern);
			logParser = log4jPatternParser;
		}

		return logParser;
	}

	public static LogParser getLogParser(LogFileType logFileType, Locale locale, TimeZone displayTimezone) {
		LogParser logParser = null;

		LogType logType = logFileType.getLogType();

		switch (logType) {

		case PEGA_ALERT:
			logParser = new AlertLogParser(logFileType, locale);
			break;
		case PEGA_RULES:
			logParser = new Log4jPatternParser(logFileType, locale, displayTimezone);
			// TODO implement other types as well
		case JBOSS:
			break;
		case WAS:
			break;
		case WLS:
			break;
		default:
			break;
		}

		return logParser;
	}

	protected int incrementAndGetProcessedCount() {

		if (resetProcessedCount) {
			processedCount.set(0);
			resetProcessedCount = false;
		}

		return processedCount.incrementAndGet();
	}

	protected void resetProcessedCount() {
		resetProcessedCount = true;
	}

	public int getProcessedCount() {
		return processedCount.get();
	}
}
