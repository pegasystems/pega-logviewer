
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0024ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0024ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Loading declarative network has exceeded the threshold of 1,000 ms: 17,331 ms. Type: Rule-Declare-Expressions  C"
                + "lass: abcdbank-FW-Money-Work-CustomerService";

        LOG.info("dataText: " + dataText);

        PEGA0024ReportModel pega0024ReportModel = new PEGA0024ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0024ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("abcdbank-FW-Money-Work-CustomerService",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Loading declarative network has exceeded the threshold of 1,000 ms: 1,121 ms. Type: Rule-Declare-Expressions  Cl"
                + "ass: Data-Admin-Toggle-Operator";

        LOG.info("dataText: " + dataText);

        PEGA0024ReportModel pega0024ReportModel = new PEGA0024ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0024ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Data-Admin-Toggle-Operator", alertMessageReportEntryKey);

    }
}
