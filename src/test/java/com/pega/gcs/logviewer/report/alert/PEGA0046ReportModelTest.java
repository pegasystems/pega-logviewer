
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0046ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0046ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Queue entry not yet started (or a small chance it failed), Proceeding to load data page Declare_CaseTree having "
                + "pzInsKey RULE-DECLARE-PAGES DECLARE_CASETREE #20140903T125943.492 GMT in user thread";

        LOG.info("dataText: " + dataText);

        PEGA0046ReportModel pega0046ReportModel = new PEGA0046ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0046ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Declare_CaseTree", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Queue entry not yet started (or a small chance it failed), Proceeding to load in user thread. DP : D_AccountDeta"
                + "ils whose inskey is RULE-DECLARE-PAGES D_ACCOUNTDETAILS #20160414T154016.561 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0046ReportModel pega0046ReportModel = new PEGA0046ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0046ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("D_AccountDetails", alertMessageReportEntryKey);
    }
}
