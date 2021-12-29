
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0026ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0026ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Time to acquire a connection has exceeded the alert threshold of 100 ms: 15578 ms. DBName [pegadata] Initial [Fa"
                + "lse] Connection [7] Status [New] Type [JNDI Name Common] Conn Mgr [App Server] Activate Time [0] DBInfoMap Time [0] Conn"
                + "ection Count [24]";

        LOG.info("dataText: " + dataText);

        PEGA0026ReportModel pega0026ReportModel = new PEGA0026ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0026ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pegadata", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Time to acquire a connection has exceeded the alert threshold of 100 ms: 143 ms. DBName [externalmktdata] Initia"
                + "l [False] Connection [0] Status [New] Type [JNDI Name Common] Conn Mgr [App Server] Activate Time [0] DBInfoMap Time [0]"
                + " Connection Count [14]";

        LOG.info("dataText: " + dataText);

        PEGA0026ReportModel pega0026ReportModel = new PEGA0026ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0026ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("externalmktdata", alertMessageReportEntryKey);

    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Time to acquire a connection has exceeded the alert threshold of 10000 ms: 11454 ms. DBName [pegarules] Connecti"
                + "on [2229747] Status [New]{ \"dataSource\": \"java:comp/env/jdbc/PegaRULES\"}[Common] Conn Mgr [Pega] Activate Time [1145"
                + "5] DBInfoMap Time [0] Connection Count [0]";

        LOG.info("dataText: " + dataText);

        PEGA0026ReportModel pega0026ReportModel = new PEGA0026ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0026ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pegarules", alertMessageReportEntryKey);

    }

}
