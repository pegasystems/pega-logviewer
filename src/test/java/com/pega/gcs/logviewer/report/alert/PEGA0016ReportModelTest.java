
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0016ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0016ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "collections/mru/PropertyReference: Attempted to agressively reduce MRU from: 18750 by 9750 to 9000; actually rem"
                + "oved 9750 entries leaving ~9000.";

        LOG.info("dataText: " + dataText);

        PEGA0016ReportModel pega0016ReportModel = new PEGA0016ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0016ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("collections/mru/PropertyReference", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "fua/global: Successfully reduced MRU by 4001 to: 16000.";

        LOG.info("dataText: " + dataText);

        PEGA0016ReportModel pega0016ReportModel = new PEGA0016ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0016ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("fua/global", alertMessageReportEntryKey);

    }
}
