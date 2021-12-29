
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0044ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0044ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Reached threshold limit for message ID: PEGA0011, will send again after Thu May 16 11:34:18 CEST 2019";

        LOG.info("dataText: " + dataText);

        PEGA0044ReportModel pega0044ReportModel = new PEGA0044ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0044ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PEGA0011", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Reached threshold limit for message ID: PEGA0017, will send again after Wed Jul 27 06:37:18 UTC 2016";

        LOG.info("dataText: " + dataText);

        PEGA0044ReportModel pega0044ReportModel = new PEGA0044ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0044ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PEGA0017", alertMessageReportEntryKey);
    }
}
