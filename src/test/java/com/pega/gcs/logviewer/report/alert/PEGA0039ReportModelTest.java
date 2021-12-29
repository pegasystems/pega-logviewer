
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0039ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0039ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Read blob from database with compressed size 3084030 expanded size 14893172. pzInsKey: ABCD-TOPAZ-NGT-WORK NGT-2"
                + "0160301-34";

        LOG.info("dataText: " + dataText);

        PEGA0039ReportModel pega0039ReportModel = new PEGA0039ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0039ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("ABCD-TOPAZ-NGT-WORK NGT-20160301-34",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Read blob from database with compressed size 5858416 expanded size 11944976. pzInsKey: DATA-WORKATTACH-FILE ABC-"
                + "CS-WORK-INTERACTION IM-4536!20190227T091618.497 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0039ReportModel pega0039ReportModel = new PEGA0039ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0039ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "DATA-WORKATTACH-FILE ABC-CS-WORK-INTERACTION IM-4536!20190227T091618.497 GMT",
                alertMessageReportEntryKey);
    }
}
