
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0103ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0103ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Queue processor run failed : some reason ";

        LOG.info("dataText: " + dataText);

        PEGA0103ReportModel pega0103ReportModel = new PEGA0103ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0103ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("some reason", alertMessageReportEntryKey);
    }

}
