
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0040ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0040ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Wrote blob to database with compressed size 4857065. pzInsKey: DATA-WORKATTACH-FILE ABC-CS-WORK-INTERACTION IM-7"
                + "116!20190315T075857.673 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0040ReportModel pega0040ReportModel = new PEGA0040ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0040ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "DATA-WORKATTACH-FILE ABC-CS-WORK-INTERACTION IM-7116!20190315T075857.673 GMT",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Wrote blob to database with compressed size 5319760. pzInsKey: ABCD-TOPAZ-NGT-WORK NGT-20160712-17";

        LOG.info("dataText: " + dataText);

        PEGA0040ReportModel pega0040ReportModel = new PEGA0040ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0040ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("ABCD-TOPAZ-NGT-WORK NGT-20160712-17",
                alertMessageReportEntryKey);
    }
}
