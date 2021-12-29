
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0018ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0018ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "The requestor H26D87BB280F7A279B10610DED3DD40F1 has exceeded the PRThread count threshold by creating the PRThre"
                + "ad TABTHREAD3; there are now 7 PRThreads in the requestor. [TABTHREAD0, Developer, TABTHREAD2, TABTHREAD1, TABTHREAD3, O"
                + "penPortal, STANDARD]";

        LOG.info("dataText: " + dataText);

        PEGA0018ReportModel pega0018ReportModel = new PEGA0018ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0018ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("H26D87BB280F7A279B10610DED3DD40F1", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "The requestor H60B9CB64B025F3EC4755F1F6A70D5340 has exceeded the PRThread count threshold by creating the PRThre"
                + "ad pyNS_CPMPortal11_CPMWorkThread; there are now 21 PRThreads in the requestor. [pyNS_CPMPortal0_HomeTab, pyNS_CPMPortal"
                + "2_CPMWorkThread, pyNS_CPMPortal3_CPMWorkThread, pyNS_CPMPortal4_CPMWorkThread, pyNS_CPMPortal20_CPMWorkThread, pyNS_CPMP"
                + "ortal10_CPMWorkThread, pyNS_CPMPortal5_CPMWorkThread, pyNS_CPMPortal21_CPMWorkThread, pyNS_CPMPortal11_CPMWorkThread, py"
                + "NS_CPMPortal6_CPMWorkThread, pyNS_CPMPortal22_CPMWorkThread, pyNS_CPMPortal7_CPMWorkThread, pyNS_CPMPortal23_CPMWorkThre"
                + "ad, pyNS_CPMPortal8_CPMWorkThread, pyNS_CPMPortal24_CPMWorkThread, pyNS_CPMPortal9_CPMWorkThread, pyNS_CPMPortal25_CPMWo"
                + "rkThread, pyNS_CPMPortal26_CPMWorkThread, pyNS_CPMPortal27_CPMWorkThread, STANDARD, pyNS_CPMPortal19_CPMWorkThread]";

        LOG.info("dataText: " + dataText);

        PEGA0018ReportModel pega0018ReportModel = new PEGA0018ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0018ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("H60B9CB64B025F3EC4755F1F6A70D5340", alertMessageReportEntryKey);

    }
}
