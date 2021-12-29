
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0065ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0065ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "";

        LOG.info("dataText: " + dataText);

        PEGA0065ReportModel pega0065ReportModel = new PEGA0065ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0065ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);
        org.junit.jupiter.api.Assertions.fail();
    }

}
