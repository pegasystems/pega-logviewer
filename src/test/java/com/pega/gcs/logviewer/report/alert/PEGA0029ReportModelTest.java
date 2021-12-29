
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0029ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0029ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Stream exceeded client response byte threshold (pxObjClass=Rule-HTML-Section;pyStreamName=CPMInboundEmail;).";

        LOG.info("dataText: " + dataText);

        PEGA0029ReportModel pega0029ReportModel = new PEGA0029ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0029ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rule-HTML-Section.CPMInboundEmail", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Stream exceeded client response byte threshold (pxObjClass=Rule-HTML-Section;pyStreamName=ShowCommentList;).";

        LOG.info("dataText: " + dataText);

        PEGA0029ReportModel pega0029ReportModel = new PEGA0029ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0029ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rule-HTML-Section.ShowCommentList", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Stream exceeded client response byte threshold (pxObjClass=Rule-Alias-Function;pyPurpose=pxSubstring;).";

        LOG.info("dataText: " + dataText);

        PEGA0029ReportModel pega0029ReportModel = new PEGA0029ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0029ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rule-Alias-Function.pxSubstring", alertMessageReportEntryKey);
    }
}
