
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0084ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0084ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Not enough active shards for index rule to meet write consistency of quorum ";

        LOG.info("dataText: " + dataText);

        PEGA0084ReportModel pega0084ReportModel = new PEGA0084ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0084ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("rule", alertMessageReportEntryKey);
    }

}
