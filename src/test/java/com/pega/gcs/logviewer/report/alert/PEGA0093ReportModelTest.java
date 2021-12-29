
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0093ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0093ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Distinct Values query took more than the threshold of 500 ms: 2,021 ms SQL: SELECT DISTINCT \"PC0\"";

        LOG.info("dataText: " + dataText);

        PEGA0093ReportModel pega0093ReportModel = new PEGA0093ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0093ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("SELECT DISTINCT \"PC0\"", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Distinct Values query took more than the threshold of 500 ms: 1,323 ms\tSQL: Some SQL\tinserts: <ins1><ins2>";

        LOG.info("dataText: " + dataText);

        PEGA0093ReportModel pega0093ReportModel = new PEGA0093ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0093ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Some SQL", alertMessageReportEntryKey);
    }
}
