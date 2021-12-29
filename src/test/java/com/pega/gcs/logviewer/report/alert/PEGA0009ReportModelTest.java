
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0009ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0009ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "PegaRULES initialization failed. Server: application System: PEGA-DEV NodeID: 22d3438667d423f990bad6966b323df9 E"
                + "rrorMsg: ";

        LOG.info("dataText: " + dataText);

        PEGA0009ReportModel pega0009ReportModel = new PEGA0009ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0009ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PEGA-DEV", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "PegaRULES initialization failed. Server: abcde120.abc.com System: abc_prod NodeID: cab20235256e6e6a78b884f299728"
                + "846e ErrorMsg: PRNodeImpl init failed";

        LOG.info("dataText: " + dataText);

        PEGA0009ReportModel pega0009ReportModel = new PEGA0009ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0009ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("abc_prod", alertMessageReportEntryKey);
    }
}
