
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0088ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0088ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "member" + " has disconnected from the cluster.";

        LOG.info("dataText: " + dataText);

        PEGA0088ReportModel pega0088ReportModel = new PEGA0088ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0088ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);
        org.junit.jupiter.api.Assertions.fail();
    }

}
