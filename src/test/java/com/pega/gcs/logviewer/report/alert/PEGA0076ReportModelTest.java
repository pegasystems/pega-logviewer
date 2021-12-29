
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0076ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0076ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Node adm.042b22b94ae8c10c9 of service Aggregation.Default became UNREACHABLE";

        LOG.info("dataText: " + dataText);

        PEGA0076ReportModel pega0076ReportModel = new PEGA0076ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0076ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("adm.042b22b94ae8c10c9 - Aggregation.Default",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Node decisionhub-i-0e4f9127431b8e168 of service Aggregation.Default became UNREACHABLE";

        LOG.info("dataText: " + dataText);

        PEGA0076ReportModel pega0076ReportModel = new PEGA0076ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0076ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("decisionhub-i-0e4f9127431b8e168 - Aggregation.Default",
                alertMessageReportEntryKey);
    }
}
