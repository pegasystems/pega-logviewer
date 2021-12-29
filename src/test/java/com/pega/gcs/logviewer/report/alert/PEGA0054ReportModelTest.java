
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0054ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0054ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "";

        LOG.info("dataText: " + dataText);

        PEGA0054ReportModel pega0054ReportModel = new PEGA0054ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0054ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.fail("");
    }

}
