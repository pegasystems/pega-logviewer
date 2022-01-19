
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0055ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0055ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "This node's [SERVER@localhost:0] clock has drifted beyond configured threshold of 10 SECONDS, and has the newer "
                + "time in the cluster; the actual delta is 10 SECONDS.";

        LOG.info("dataText: " + dataText);

        PEGA0055ReportModel pega0055ReportModel = new PEGA0055ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0055ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("[SERVER@localhost:0]", alertMessageReportEntryKey);

    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "This node's [outbound_12.34.567.890] clock has drifted beyond configured threshold of 10 sec, and has the oldest"
                + " time in the cluster; the actual delta is 16,51 seconds.";

        LOG.info("dataText: " + dataText);

        PEGA0055ReportModel pega0055ReportModel = new PEGA0055ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0055ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("[outbound_12.34.567.890]", alertMessageReportEntryKey);

    }
}
