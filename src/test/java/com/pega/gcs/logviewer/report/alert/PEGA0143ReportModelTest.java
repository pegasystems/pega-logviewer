
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0143ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0143ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Maximum error records reached for listener myEmailListener with count: 5 in Log-Service-Email table";

        LOG.info("dataText: " + dataText);

        PEGA0143ReportModel pega0143ReportModel = new PEGA0143ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0143ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("myEmailListener", alertMessageReportEntryKey);
    }

}
