
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0034ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0034ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Number of indexes written for instance DATA-ADMIN-OPERATOR-ID U_2416E exceeded threshold of 100 : 103";

        LOG.info("dataText: " + dataText);

        PEGA0034ReportModel pega0034ReportModel = new PEGA0034ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0034ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("DATA-ADMIN-OPERATOR-ID U_2416E", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Number of indexes written for instance RULE-HTML-SECTION DATA-PORTAL PYWORKLISTCOMBINEDWIDGETGRIDSMAIN #20190304"
                + "T144641.559 GMT exceeded threshold of 100 : 113";

        LOG.info("dataText: " + dataText);

        PEGA0034ReportModel pega0034ReportModel = new PEGA0034ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0034ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-HTML-SECTION DATA-PORTAL PYWORKLISTCOMBINEDWIDGETGRIDSMAIN #20190304T144641.559 GMT",
                alertMessageReportEntryKey);
    }
}
