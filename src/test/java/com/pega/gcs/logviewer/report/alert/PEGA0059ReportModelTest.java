
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0059ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0059ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Writing the interaction history fact record(s) took more than the threshold of 1000 ms : 1122 ms for subject id("
                + "s) [373290974]";

        LOG.info("dataText: " + dataText);

        PEGA0059ReportModel pega0059ReportModel = new PEGA0059ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0059ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("373290974", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Writing the interaction history fact record(s) took more than the threshold of 1000 ms : 3805 ms for subject id("
                + "s) [MPER8554427,MPER8343494,MPER8737317,MPER9017656,MPER10792426,04002168,MPER8560362,0401564559,MPER8669392,MPER9544124"
                + ",MPER11986543,MPER11014354,MPER11229160,MPER9684738,MPER11320025,MPER10841900,0443347103,0503049566,MPER10661804,MPER886"
                + "5727,0401721199,0400742680,0503612737,MPER11391353,MPER11967871,0442443951]";

        LOG.info("dataText: " + dataText);

        PEGA0059ReportModel pega0059ReportModel = new PEGA0059ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0059ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "MPER8554427,MPER8343494,MPER8737317,MPER9017656,MPER10792426,04002168,MPER8560362,0401564559,MPER8669392,MPER9544124,MPER1"
                        + "1986543,MPER11014354,MPER11229160,MPER9684738,MPER11320025,MPER10841900,0443347103,0503049566,MPER10661804,MPER8"
                        + "865727,0401721199,0400742680,0503612737,MPER11391353,MPER11967871,0442443951",
                alertMessageReportEntryKey);
    }
}
