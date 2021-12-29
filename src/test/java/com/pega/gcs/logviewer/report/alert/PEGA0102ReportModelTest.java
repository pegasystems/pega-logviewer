
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0102ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0102ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Job Scheduler \"abcd\" activity \"PegaMKT-Data-Customer:someActivity\" execution failed in node abgtssdfsf which"
                + " will cause delayed Queue Processor use cases to stop processing";

        LOG.info("dataText: " + dataText);

        PEGA0102ReportModel pega0102ReportModel = new PEGA0102ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0102ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("abcd [PegaMKT-Data-Customer:someActivity]",
                alertMessageReportEntryKey);
    }

}
