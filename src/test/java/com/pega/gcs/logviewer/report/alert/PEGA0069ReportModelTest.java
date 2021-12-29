
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0069ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0069ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "ClientPageLoad has exceeded the elapsed time alert threshold of 4000 ms: total duration 13262 ms, client time 31"
                + "59 ms, network+server time 10103 ms, server time 8809 ms, #calls 4, call durations 995 1114 1672 7436, server interactio"
                + "ns 4, load description pyActivity:%40baseclass.doUIAction|harnessName:pzLPStrategyHierarchy|className:Pega-Landing-Decis"
                + "ion-StrategyHierarchy|action:Display";

        LOG.info("dataText: " + dataText);

        PEGA0069ReportModel pega0069ReportModel = new PEGA0069ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0069ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "pyActivity:%40baseclass.doUIAction|harnessName:pzLPStrategyHierarchy|className:Pega-Landing-Decision-StrategyHierarchy|act"
                        + "ion:Display",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "ClientPageLoad has exceeded the elapsed time alert threshold of 4000 ms: load duration 79263 ms, server duration"
                + " 9738 ms, server interactions 59, load description action:FinishAssignment.";

        LOG.info("dataText: " + dataText);

        PEGA0069ReportModel pega0069ReportModel = new PEGA0069ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0069ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("action:FinishAssignment.", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "ClientPageLoad has exceeded the elapsed time alert threshold of 4000 ms: total duration 5603 ms, client time 442"
                + "5 ms, network+server time 1178 ms, server time 4029 ms, #calls 8, call durations 3949 139 157 168 893 22 42 75, server i"
                + "nteractions 8, load description action:pyActivity:FinishAssignment";

        LOG.info("dataText: " + dataText);

        PEGA0069ReportModel pega0069ReportModel = new PEGA0069ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0069ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("action:pyActivity:FinishAssignment", alertMessageReportEntryKey);
    }
}
