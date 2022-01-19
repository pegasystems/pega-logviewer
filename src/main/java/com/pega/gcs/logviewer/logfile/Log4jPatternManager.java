
package com.pega.gcs.logviewer.logfile;

import java.util.Set;
import java.util.TreeSet;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class Log4jPatternManager {

    private static final Log4j2Helper LOG = new Log4j2Helper(Log4jPatternManager.class);

    private static Log4jPatternManager _INSTANCE;

    private Set<Log4jPattern> defaultRulesLog4jPatternSet;

    private Set<Log4jPattern> defaultClusterLog4jPatternSet;

    private Log4jPattern socketRecieverLog4jPattern;

    private Log4jPatternManager() {

        defaultRulesLog4jPatternSet = new TreeSet<Log4jPattern>();

        defaultClusterLog4jPatternSet = new TreeSet<Log4jPattern>();

        LogPatternFactory logPatternFactory = LogPatternFactory.getInstance();

        String name;
        String patternString;
        Log4jPattern log4jPattern;

        // Cloud Watch logs - 8.6.x
        name = "CW Logs 8.6.x";
        patternString = "${CW_LOG} %d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId"
                + "} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, true);
        // defaultPegaRulesLog4jPatternSet.add(PATTERN_CW_86x);

        // 8.6.x
        name = "8.6.x";
        patternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} "
                + "%X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        // defaultPegaRulesLog4jPatternSet.add(PATTERN_86x);

        // Cloud Watch logs - 7.x
        name = "CW Logs 7.x";
        patternString = "${CW_LOG} %d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, true);
        defaultRulesLog4jPatternSet.add(log4jPattern);

        // 7.1.5
        name = "7.x";
        patternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        defaultRulesLog4jPatternSet.add(log4jPattern);

        // 6.3.1
        name = "5.4.x";
        patternString = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        defaultRulesLog4jPatternSet.add(log4jPattern);

        // 5.2 SP1
        name = "5.2.x";
        patternString = "%d{ABSOLUTE} [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        defaultRulesLog4jPatternSet.add(log4jPattern);

        // CTI
        name = "CTI";
        patternString = "%d [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        defaultRulesLog4jPatternSet.add(log4jPattern);

//        // Fringe Utilities
//        name = "Fringe Utilities";
//        patternString = "%d [%30.30t] (%50.50c{32}) [%-5p] - %m%n";
//        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
//        defaultRulesLog4jPatternSet.add(log4jPattern);

        // CUSTOM1
        // name = "CUSTOM1";
        // patternString = "%d{dd HH:mm:ss,SSS} [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} -
        // %m%n";
        // log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        // defaultRulesLog4jPatternSet.add(log4jPattern);

        // CLUSTER Log Pattern

        // Cloud Watch logs - 8.6.x - CLUSTER
        name = "CW Logs 8.6.x - CLUSTER";
        patternString = "${CW_LOG} %d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, true);
        defaultClusterLog4jPatternSet.add(log4jPattern);

        // 8.6.x - CLUSTER
        name = "8.6.x - CLUSTER";
        patternString = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        defaultClusterLog4jPatternSet.add(log4jPattern);

        // Cloud Watch logs - 7.1.5 - CLUSTER
        name = "CW Logs 7.x - CLUSTER";
        patternString = "${CW_LOG} %d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, true);
        defaultClusterLog4jPatternSet.add(log4jPattern);

        // 7.1.5 - CLUSTER
        name = "7.x - CLUSTER";
        patternString = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        defaultClusterLog4jPatternSet.add(log4jPattern);

        // SOCKET RECIEVER
        name = "7.x - SOCKET_RECIEVER";
        patternString = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        socketRecieverLog4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
    }

    public static Log4jPatternManager getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new Log4jPatternManager();
        }

        return _INSTANCE;
    }

    public Set<Log4jPattern> getDefaultRulesLog4jPatternSet() {
        return defaultRulesLog4jPatternSet;
    }

    public Set<Log4jPattern> getDefaultClusterLog4jPatternSet() {
        return defaultClusterLog4jPatternSet;
    }

    public Log4jPattern getSocketRecieverLog4jPattern() {
        return socketRecieverLog4jPattern;
    }

    public static void main(String[] args) {

        Log4jPatternManager log4jPatternManager = Log4jPatternManager.getInstance();

        Set<Log4jPattern> defaultPegaRulesLog4jPatternSet = log4jPatternManager.getDefaultRulesLog4jPatternSet();

        for (Log4jPattern logPattern : defaultPegaRulesLog4jPatternSet) {
            LOG.info(logPattern);
        }

        LOG.info("----------");

        Set<Log4jPattern> defaultPegaClusterLog4jPatternSet = log4jPatternManager.getDefaultClusterLog4jPatternSet();

        for (Log4jPattern logPattern : defaultPegaClusterLog4jPatternSet) {
            LOG.info(logPattern);
        }
    }
}
