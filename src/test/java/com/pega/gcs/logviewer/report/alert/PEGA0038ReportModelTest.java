
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0038ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0038ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "";

        LOG.info("dataText: " + dataText);

        PEGA0038ReportModel pega0038ReportModel = new PEGA0038ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0038ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);
        org.junit.jupiter.api.Assertions.fail("");
    }

}
