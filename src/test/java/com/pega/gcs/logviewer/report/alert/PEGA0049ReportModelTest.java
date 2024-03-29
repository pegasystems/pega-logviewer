
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0049ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0049ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Lucene search feature is unavailable for Rules";

        LOG.info("dataText: " + dataText);

        PEGA0049ReportModel pega0049ReportModel = new PEGA0049ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0049ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rules", alertMessageReportEntryKey);

    }

}
