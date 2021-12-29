
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0098ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0098ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Job Scheduler \"abcd\" registration failed";

        LOG.info("dataText: " + dataText);

        PEGA0098ReportModel pega0098ReportModel = new PEGA0098ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0098ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("\"abcd\"", alertMessageReportEntryKey);
    }

}
