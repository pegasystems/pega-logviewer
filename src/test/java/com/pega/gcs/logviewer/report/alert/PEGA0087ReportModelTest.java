
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0087ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0087ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "REST Service PEGAMKTCONTAINER!V3!CONTAINER0E7278CDA02A9C0446DCC75A4DF34EB2 circuit breaker tripped due to 3 cons"
                + "ecutive timeouts.";

        LOG.info("dataText: " + dataText);

        PEGA0087ReportModel pega0087ReportModel = new PEGA0087ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0087ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "REST - PEGAMKTCONTAINER!V3!CONTAINER0E7278CDA02A9C0446DCC75A4DF34EB2", alertMessageReportEntryKey);
    }

}
