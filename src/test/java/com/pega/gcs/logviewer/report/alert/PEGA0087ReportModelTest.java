
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0087ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0087ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "serviceType Service serviceInsName circuit breaker tripped due to maxConsecutiveFailures consecutive timeouts.";

        LOG.info("dataText: " + dataText);

        PEGA0087ReportModel pega0087ReportModel = new PEGA0087ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0087ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);
        org.junit.jupiter.api.Assertions.fail();
    }

}
