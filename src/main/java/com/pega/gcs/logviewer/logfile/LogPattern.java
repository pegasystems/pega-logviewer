/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.logfile;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class LogPattern implements Serializable, Comparable<LogPattern> {

    private static final long serialVersionUID = -3027272357446490017L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogPattern.class);

    private static final LogPattern PATTERN_CW_86x;
    private static final LogPattern PATTERN_CW_715;

    private static final LogPattern PATTERN_86x;
    private static final LogPattern PATTERN_715;
    private static final LogPattern PATTERN_54x;
    private static final LogPattern PATTERN_52x;
    private static final LogPattern PATTERN_CTI;
    private static final LogPattern PATTERN_FRINGEUTILITIES;

    private static final LogPattern PATTERN_CW_86x_CLUSTER;
    private static final LogPattern PATTERN_CW_715_CLUSTER;

    private static final LogPattern PATTERN_86x_CLUSTER;
    private static final LogPattern PATTERN_715_CLUSTER;

    private static Set<LogPattern> defaultPegaRulesLog4jPatternSet;

    private static Set<LogPattern> defaultPegaClusterLog4jPatternSet;

    private String name;

    private String patternString;

    private boolean isCW;

    static {

        defaultPegaRulesLog4jPatternSet = new TreeSet<LogPattern>();

        String name;
        String patternString;

        // Cloud Watch logs - 8.6.x
        name = "CW Logs 8.6.x";
        patternString = "* %d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId"
                + "} %X{userid} - %m%n";
        PATTERN_CW_86x = new LogPattern(name, patternString, true);
        // defaultPegaRulesLog4jPatternSet.add(PATTERN_CW_86x);

        // 8.6.x
        name = "8.6.x";
        patternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} "
                + "%X{userid} - %m%n";
        PATTERN_86x = new LogPattern(name, patternString, false);
        // defaultPegaRulesLog4jPatternSet.add(PATTERN_86x);

        // Cloud Watch logs - 7.x
        name = "CW Logs 7.x";
        patternString = "* %d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_CW_715 = new LogPattern(name, patternString, true);
        defaultPegaRulesLog4jPatternSet.add(PATTERN_CW_715);

        // 7.1.5
        name = "7.x";
        patternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_715 = new LogPattern(name, patternString, false);
        defaultPegaRulesLog4jPatternSet.add(PATTERN_715);

        // 6.3.1
        name = "5.4.x";
        patternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_54x = new LogPattern(name, patternString, false);
        defaultPegaRulesLog4jPatternSet.add(PATTERN_54x);

        // 5.2 SP1
        name = "5.2.x";
        patternString = "%d{ABSOLUTE} [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_52x = new LogPattern(name, patternString, false);
        defaultPegaRulesLog4jPatternSet.add(PATTERN_52x);

        // CTI
        name = "CTI";
        patternString = "%d [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_CTI = new LogPattern(name, patternString, false);
        defaultPegaRulesLog4jPatternSet.add(PATTERN_CTI);

        // Fringe Utilities
        name = "Fringe Utilities";
        patternString = "%d [%30.30t] (%50.50c{32}) [%-5p] - %m%n";
        PATTERN_FRINGEUTILITIES = new LogPattern(name, patternString, false);
        defaultPegaRulesLog4jPatternSet.add(PATTERN_FRINGEUTILITIES);

        // CUSTOM1
        name = "CUSTOM1";
        patternString = "%d{dd HH:mm:ss,SSS} [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} - %m%n";
        LogPattern log4jPattern = new LogPattern(name, patternString, false);
        defaultPegaRulesLog4jPatternSet.add(log4jPattern);

        // CLUSTER Log Pattern
        defaultPegaClusterLog4jPatternSet = new TreeSet<LogPattern>();

        // Cloud Watch logs - 8.6.x - CLUSTER
        name = "CW Logs 8.6.x - CLUSTER";
        patternString = "* %d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} %X{userid} - %m%n";
        PATTERN_CW_86x_CLUSTER = new LogPattern(name, patternString, true);
        defaultPegaClusterLog4jPatternSet.add(PATTERN_CW_86x_CLUSTER);

        // 8.6.x - CLUSTER
        name = "8.6.x - CLUSTER";
        patternString = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} %X{userid} - %m%n";
        PATTERN_86x_CLUSTER = new LogPattern(name, patternString, false);
        defaultPegaClusterLog4jPatternSet.add(PATTERN_86x_CLUSTER);

        // Cloud Watch logs - 7.1.5 - CLUSTER
        name = "CW Logs 7.x - CLUSTER";
        patternString = "* %d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_CW_715_CLUSTER = new LogPattern(name, patternString, true);
        defaultPegaClusterLog4jPatternSet.add(PATTERN_CW_715_CLUSTER);

        // 7.1.5 - CLUSTER
        name = "7.x - CLUSTER";
        patternString = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        PATTERN_715_CLUSTER = new LogPattern(name, patternString, false);
        defaultPegaClusterLog4jPatternSet.add(PATTERN_715_CLUSTER);

    }

    private LogPattern() {
        // for kryo
    }

    public LogPattern(String name, String logPatternString, boolean isCW) {
        super();
        this.name = name;
        this.patternString = logPatternString;
        this.isCW = isCW;
    }

    public String getName() {
        return name;
    }

    public String getPatternString() {
        return patternString;
    }

    public boolean isCW() {
        return isCW;
    }

    private int getGroupCount() {

        int groupCount = 0;

        if ((patternString != null) && (!"".equals(patternString))) {

            PatternParser patternParser = new PatternParser("Converter");

            List<PatternFormatter> patternFormatterList = patternParser.parse(patternString);

            groupCount = patternFormatterList.size();
        }

        return groupCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(patternString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LogPattern)) {
            return false;
        }
        LogPattern other = (LogPattern) obj;
        return Objects.equals(patternString, other.patternString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder displayString = new StringBuilder();
        displayString.append(name);
        displayString.append(" [ Group Count(" + getGroupCount() + ")]");
        displayString.append(" [");
        displayString.append(patternString);
        displayString.append("]");

        return displayString.toString();
    }

    @Override
    public int compareTo(LogPattern other) {

        // group count
        int groupCount = getGroupCount();
        int otherGroupCount = other.getGroupCount();

        if (groupCount != otherGroupCount) {
            // reverse order
            return Integer.valueOf(otherGroupCount).compareTo(Integer.valueOf(groupCount));
        }

        // then pattern string
        String patternString = getPatternString();
        String otherPatternString = other.getPatternString();

        return patternString.compareTo(otherPatternString);
    }

    public static LogPattern getPattern86x() {
        return PATTERN_86x;
    }

    public static LogPattern getPattern715() {
        return PATTERN_715;
    }

    public static Set<LogPattern> getDefaultPegaRulesLog4jPatternSet() {
        return defaultPegaRulesLog4jPatternSet;
    }

    public static Set<LogPattern> getDefaultPegaClusterLog4jPatternSet() {
        return defaultPegaClusterLog4jPatternSet;
    }

    public static void main(String[] args) {

        Set<LogPattern> defaultPegaRulesLog4jPatternSet = LogPattern.getDefaultPegaRulesLog4jPatternSet();

        for (LogPattern logPattern : defaultPegaRulesLog4jPatternSet) {
            LOG.info(logPattern);
        }

        LOG.info("----------");

        Set<LogPattern> defaultPegaClusterLog4jPatternSet = LogPattern.getDefaultPegaClusterLog4jPatternSet();

        for (LogPattern logPattern : defaultPegaClusterLog4jPatternSet) {
            LOG.info(logPattern);
        }
    }
}
