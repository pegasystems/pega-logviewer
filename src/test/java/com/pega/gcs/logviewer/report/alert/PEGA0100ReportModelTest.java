
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0100ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0100ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Queue processor registration failed because : Data flow service initialization failed";

        LOG.info("dataText: " + dataText);

        PEGA0100ReportModel pega0100ReportModel = new PEGA0100ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0100ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Data flow service initialization failed",
                alertMessageReportEntryKey);
    }

}
