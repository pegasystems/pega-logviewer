
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0035ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0035ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "The number of elements in this clipboard list property has exceeded WARN level.  Maximum size: 10000 Property re"
                + "ference: .pxResults";

        LOG.info("dataText: " + dataText);

        PEGA0035ReportModel pega0035ReportModel = new PEGA0035ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0035ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(".pxResults", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "The number of elements in this clipboard list property has exceeded WARN level. Maximum size: 10000 Property ref"
                + "erence: Event.pzRSP(1).pzRBL";

        LOG.info("dataText: " + dataText);

        PEGA0035ReportModel pega0035ReportModel = new PEGA0035ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0035ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Event.pzRSP(1).pzRBL", alertMessageReportEntryKey);
    }
}
