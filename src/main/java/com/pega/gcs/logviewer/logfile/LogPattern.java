/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.logfile;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

public class LogPattern implements Serializable, Comparable<LogPattern> {

	private static final long serialVersionUID = -3027272357446490017L;

	private String name;

	private String logPatternString;

	private LogPattern() {
		// for kryo
	}

	public LogPattern(String name, String logPatternString) {
		super();
		this.name = name;
		this.logPatternString = logPatternString;
	}

	public String getName() {
		return name;
	}

	public String getLogPatternString() {
		return logPatternString;
	}

	public int getGroupCount() {

		int groupCount = 0;

		if ((logPatternString != null) && (!"".equals(logPatternString))) {

			PatternParser patternParser = new PatternParser("Converter");

			List<PatternFormatter> patternFormatterList = patternParser.parse(logPatternString);

			groupCount = patternFormatterList.size();
		}

		return groupCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((logPatternString == null) ? 0 : logPatternString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogPattern other = (LogPattern) obj;
		if (logPatternString == null) {
			if (other.logPatternString != null)
				return false;
		} else if (!logPatternString.equals(other.logPatternString))
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer displayString = new StringBuffer();
		displayString.append(name);
		displayString.append(" [ Group Count(" + getGroupCount() + ")]");
		displayString.append(" [");
		displayString.append(logPatternString);
		displayString.append("]");

		return displayString.toString();
	}

	@Override
	public int compareTo(LogPattern o) {

		// group count
		int groupCount = getGroupCount();
		int otherGroupCount = o.getGroupCount();

		if (groupCount != otherGroupCount) {
			// reverse order
			return new Integer(otherGroupCount).compareTo(new Integer(groupCount));
		}

		// then pattern string
		String patternString = getLogPatternString();
		String otherPatternString = o.getLogPatternString();

		return patternString.compareTo(otherPatternString);
	}

	public static Set<LogPattern> getDefaultPegaRulesLog4jPatternSet() {

		TreeSet<LogPattern> defaultPegaRulesLog4jPatternSet = new TreeSet<LogPattern>();

		String name;
		String logPatternString;

		LogPattern log4jPattern;

		// 7.1.5
		name = "7.x";
		logPatternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
		log4jPattern = new LogPattern(name, logPatternString);
		defaultPegaRulesLog4jPatternSet.add(log4jPattern);

		// 6.3.1
		name = "5.4.x";
		logPatternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} - %m%n";
		log4jPattern = new LogPattern(name, logPatternString);
		defaultPegaRulesLog4jPatternSet.add(log4jPattern);

		// 5.2 SP1
		name = "5.2.x";
		logPatternString = "%d{ABSOLUTE} [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
		log4jPattern = new LogPattern(name, logPatternString);
		defaultPegaRulesLog4jPatternSet.add(log4jPattern);

		// UBS
		name = "UBS";
		logPatternString = "%d{dd HH:mm:ss,SSS} [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} - %m%n";
		log4jPattern = new LogPattern(name, logPatternString);
		defaultPegaRulesLog4jPatternSet.add(log4jPattern);

		// CTI
		name = "CTI";
		logPatternString = "%d [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
		log4jPattern = new LogPattern(name, logPatternString);
		defaultPegaRulesLog4jPatternSet.add(log4jPattern);

		return defaultPegaRulesLog4jPatternSet;
	}
}
