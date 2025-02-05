
package com.pega.gcs.logviewer.logfile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern.LogType;

public class Log4jPatternManager {

    private static final Log4j2Helper LOG = new Log4j2Helper(Log4jPatternManager.class);

    private static Log4jPatternManager _INSTANCE;

    private Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap;

    private Log4jPattern socketRecieverLog4jPattern;

    private Log4jPatternManager() {

        log4jPatternTypeMap = new HashMap<>();

        LogPatternFactory logPatternFactory = LogPatternFactory.getInstance();

        String name;
        String patStr;
        Log4jPattern log4jPattern;

        // Cloud Watch logs - 8.6.x
        name = "CW Logs 8.6.x";
        patStr = "${CW_LOG} %d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId"
                + "} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, true);
        // defaultPegaRulesLog4jPatternSet.add(PATTERN_CW_86x);

        // 8.6.x
        name = "8.6.x";
        patStr = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} "
                + "%X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        // defaultPegaRulesLog4jPatternSet.add(PATTERN_86x);

        // Cloud Watch logs - 7.x
        name = "CW Logs 7.x";
        patStr = "${CW_LOG} %d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, true);
        addToLog4jPatternTypeMap(log4jPattern);

        // 7.1.5
        name = "7.x";
        patStr = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // 6.3.1
        name = "5.4.x";
        patStr = "%d [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // 5.2 SP1
        name = "5.2.x";
        patStr = "%d{ABSOLUTE} [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // CTI
        name = "CTI";
        patStr = "%d [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // Fringe Utilities
        name = "Fringe Utilities";
        patStr = "%d [%30.30t] (%50.50c{32}) [%-5p] - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // Kakfa
        name = "Kafka";
        patStr = "[%d] %p [%t]%m (%c)%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // CUSTOM1
        // name = "CUSTOM1";
        // patternString = "%d{dd HH:mm:ss,SSS} [%20.20t] [%10.10X{pegathread}] [%20.20X{app}] (%80.80c{3}) %-5p %X{stack} %X{userid} -
        // %m%n";
        // log4jPattern = logPatternFactory.getLog4jPattern(name, patternString, false);
        // defaultRulesLog4jPatternSet.add(log4jPattern);

        // CLUSTER Log Pattern

        // Cloud Watch logs - 8.6.x - CLUSTER
        name = "CW Logs 8.6.x - CLUSTER";
        patStr = "${CW_LOG} %d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_CLUSTER, name, patStr, true);
        addToLog4jPatternTypeMap(log4jPattern);

        // 8.6.x - CLUSTER
        name = "8.6.x - CLUSTER";
        patStr = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{RequestorId} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_CLUSTER, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // Cloud Watch logs - 7.1.5 - CLUSTER
        name = "CW Logs 7.x - CLUSTER";
        patStr = "${CW_LOG} %d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_CLUSTER, name, patStr, true);
        addToLog4jPatternTypeMap(log4jPattern);

        // 7.1.5 - CLUSTER
        name = "7.x - CLUSTER";
        patStr = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_CLUSTER, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // DATAFLOW Log Pattern

        // Cloud Watch logs - 8.6.x - DATAFLOW
        name = "CW Logs 8.6.x - DATAFLOW";
        patStr = "${CW_LOG} %d (%30.30c{3}) %-5p - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_DATAFLOW, name, patStr, true);
        addToLog4jPatternTypeMap(log4jPattern);

        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_DDSMETRIC, name, patStr, true);
        addToLog4jPatternTypeMap(log4jPattern);

        // 8.6.x - DATAFLOW
        name = "8.6.x - DATAFLOW";
        patStr = "%d (%30.30c{3}) %-5p - %m%n";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_DATAFLOW, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_DDSMETRIC, name, patStr, true);
        addToLog4jPatternTypeMap(log4jPattern);

        // SOCKET RECIEVER
        name = "7.x - SOCKET_RECIEVER";
        patStr = "%d [%20.20t] [%20.20X{tenantid}] [%20.20X{app}] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n";
        socketRecieverLog4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES, name, patStr, false);

        // PEGA_CLOUDK_ISTIO
        name = "PEGA_CLOUDK_ISTIO";
        // [%START_TIME%] \"%REQ(:METHOD)% %REQ(X-ENVOY-ORIGINAL-PATH?:PATH)% %PROTOCOL%\" %RESPONSE_CODE% %RESPONSE_FLAGS%
        // %RESPONSE_CODE_DETAILS% %CONNECTION_TERMINATION_DETAILS% \"%UPSTREAM_TRANSPORT_FAILURE_REASON%\" %BYTES_RECEIVED% %BYTES_SENT%
        // %DURATION% %RESP(X-ENVOY-UPSTREAM-SERVICE-TIME)% \"%REQ(X-FORWARDED-FOR)%\" \"%REQ(USER-AGENT)%\" \"%REQ(X-REQUEST-ID)%\"
        // \"%REQ(:AUTHORITY)%\" \"%UPSTREAM_HOST%\" %UPSTREAM_CLUSTER% %UPSTREAM_LOCAL_ADDRESS% %DOWNSTREAM_LOCAL_ADDRESS%
        // %DOWNSTREAM_REMOTE_ADDRESS% %REQUESTED_SERVER_NAME% %ROUTE_NAME%\n
        patStr = "%d{yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX} [%X{start_time}] \"%X{req_path}\" %X{response_code} %X{response_flags} %X{response_c"
                + "ode_details} %X{conn_term_details} \"%X{us_trans_fail_reason}\" %X{bytes_received} %X{bytes_sent} %X{duration} %X{resp_x"
                + "_e_us_serv_time} \"%X{req_x_forwarded_for}\" \"%X{req_user_agent}\" \"%X{req_x_request_id}\" \"%X{req_authority}\" \"%X{"
                + "us_host}\" %X{us_cluster} %X{us_local_address} %X{ds_local_address} %X{ds_remote_address} %X{requested_server_name} %X{r"
                + "oute_name}";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_CLOUDK_ISTIO, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);

        // PEGA_CLOUDK_ACCESS_LOG
        name = "PEGA_CLOUDK_ACCESS_LOG";
        // %{X-Forwarded-For}i %h %l %u %t &quot;%r&quot; %s %b %D %I
        patStr = "%d{yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX} %X{req_x_forwarded_for} %X{remote_host} %X{remote_username} %X{remote_user} [%X{st"
                + "art_time}] \"%X{req_path}\" %X{response_code} %X{bytes_sent} %X{duration} %X{req_thread_name}";
        log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_CLOUDK_ACCESS_LOG, name, patStr, false);
        addToLog4jPatternTypeMap(log4jPattern);
    }

    public static Log4jPatternManager getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new Log4jPatternManager();
        }

        return _INSTANCE;
    }

    private Map<LogType, Set<Log4jPattern>> getLog4jPatternTypeMap() {
        return log4jPatternTypeMap;
    }

    private void addToLog4jPatternTypeMap(Log4jPattern log4jPattern) {

        LogType logType = log4jPattern.getLogType();

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        Set<Log4jPattern> log4jPatternSet = log4jPatternTypeMap.get(logType);

        if (log4jPatternSet == null) {
            log4jPatternSet = new TreeSet<>();
            log4jPatternTypeMap.put(logType, log4jPatternSet);
        }

        log4jPatternSet.add(log4jPattern);
    }

    public Set<Log4jPattern> getDefaultRulesLog4jPatternSet() {

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        return Collections.unmodifiableSet(log4jPatternTypeMap.get(LogType.PEGA_RULES));
    }

    public Set<Log4jPattern> getDefaultClusterLog4jPatternSet() {

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        return Collections.unmodifiableSet(log4jPatternTypeMap.get(LogType.PEGA_CLUSTER));
    }

    public Set<Log4jPattern> getDefaultDataflowLog4jPatternSet() {

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        return Collections.unmodifiableSet(log4jPatternTypeMap.get(LogType.PEGA_DATAFLOW));
    }

    public Set<Log4jPattern> getDefaultDdsMetricLog4jPatternSet() {

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        return Collections.unmodifiableSet(log4jPatternTypeMap.get(LogType.PEGA_DDSMETRIC));
    }

    public Set<Log4jPattern> getDefaultPegaCloudKIstioLog4jPatternSet() {

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        return Collections.unmodifiableSet(log4jPatternTypeMap.get(LogType.PEGA_CLOUDK_ISTIO));
    }

    public Set<Log4jPattern> getDefaultPegaCloudKAccessLog4jPatternSet() {

        Map<LogType, Set<Log4jPattern>> log4jPatternTypeMap = getLog4jPatternTypeMap();

        return Collections.unmodifiableSet(log4jPatternTypeMap.get(LogType.PEGA_CLOUDK_ACCESS_LOG));
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
