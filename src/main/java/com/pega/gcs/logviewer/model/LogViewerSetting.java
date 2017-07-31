/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;

import com.pega.gcs.logviewer.logfile.LogPattern;

public class LogViewerSetting implements Serializable {

	private static final long serialVersionUID = -5504133950850626498L;

	private int recentItemsCount;

	private String charset;

	private boolean tailLogFile;

	private boolean reloadPreviousFiles;

	private Locale locale;

	private Set<LogPattern> pegaRuleslog4jPatternSet;

	public LogViewerSetting() {
		super();

		pegaRuleslog4jPatternSet = LogPattern.getDefaultPegaRulesLog4jPatternSet();

		setDefault();
	}

	public void setDefault() {
		recentItemsCount = 10;
		charset = Charset.defaultCharset().name();
		locale = Locale.getDefault();
		tailLogFile = false;
		reloadPreviousFiles = true;
	}

	public int getRecentItemsCount() {
		return recentItemsCount;
	}

	public void setRecentItemsCount(int recentItemsCount) {
		this.recentItemsCount = recentItemsCount;
	}

	public String getCharset() {

		if (charset == null) {
			charset = Charset.defaultCharset().name();
		}

		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isTailLogFile() {
		return tailLogFile;
	}

	public void setTailLogFile(boolean tailLogFile) {
		this.tailLogFile = tailLogFile;
	}

	public boolean isReloadPreviousFiles() {
		return reloadPreviousFiles;
	}

	public void setReloadPreviousFiles(boolean reloadPreviousFiles) {
		this.reloadPreviousFiles = reloadPreviousFiles;
	}

	public Locale getLocale() {

		if (locale == null) {
			locale = Locale.getDefault();
		}

		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Set<LogPattern> getPegaRuleslog4jPatternSet() {
		return pegaRuleslog4jPatternSet;
	}

	public void setPegaRuleslog4jPatternSet(Set<LogPattern> logPatternSet) {
		this.pegaRuleslog4jPatternSet = logPatternSet;
	}

}
