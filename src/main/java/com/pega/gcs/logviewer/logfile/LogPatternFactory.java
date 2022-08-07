
package com.pega.gcs.logviewer.logfile;

import java.util.List;

import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

import com.pega.gcs.logviewer.logfile.AbstractLogPattern.LogType;

public class LogPatternFactory {

    private static LogPatternFactory _INSTANCE;

    private PatternParser patternParser;

    private LogPatternFactory() {

        DefaultConfiguration defaultConfiguration = new DefaultConfiguration();

        patternParser = new PatternParser(defaultConfiguration, "Converter", null);
    }

    public static LogPatternFactory getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new LogPatternFactory();
        }

        return _INSTANCE;
    }

    public PatternParser getPatternParser() {
        return patternParser;
    }

    public AlertLogPattern getAlertLogPattern() {
        return new AlertLogPattern("Alert", 0, false);
    }

    public Log4jPattern getLog4jPattern(String name, String patternString, boolean isCW) {

        Log4jPattern log4jPattern = null;

        if ((patternString != null) && (!"".equals(patternString))) {

            List<PatternFormatter> patternFormatterList = patternParser.parse(patternString);

            int groupCount = patternFormatterList.size();

            log4jPattern = new Log4jPattern(LogType.PEGA_RULES, name, patternString, groupCount, isCW);
        }

        return log4jPattern;
    }

    public Log4jPattern getDataflowLog4jPattern(String name, String patternString, boolean isCW) {

        Log4jPattern log4jPattern = null;

        if ((patternString != null) && (!"".equals(patternString))) {

            List<PatternFormatter> patternFormatterList = patternParser.parse(patternString);

            int groupCount = patternFormatterList.size();

            log4jPattern = new Log4jPattern(LogType.PEGA_DATAFLOW, name, patternString, groupCount, isCW);
        }

        return log4jPattern;
    }
}
