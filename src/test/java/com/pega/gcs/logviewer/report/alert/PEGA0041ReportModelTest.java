
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0041ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0041ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Work object SCR-8513672 of class ABCD-FW-Screening-Work-Request is being written to pr_other";

        LOG.info("dataText: " + dataText);

        PEGA0041ReportModel pega0041ReportModel = new PEGA0041ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0041ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("ABCD-FW-Screening-Work-Request", alertMessageReportEntryKey);
    }

}
