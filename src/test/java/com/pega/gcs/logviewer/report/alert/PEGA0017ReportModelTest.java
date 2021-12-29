
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0017ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0017ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "declaratives/fragments: Successfully reduced Concurrent MRU Map by 401 down to: 2000.";

        LOG.info("dataText: " + dataText);

        PEGA0017ReportModel pega0017ReportModel = new PEGA0017ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0017ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("declaratives/fragments", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "collections/mru/PropertyReference: Successfully reduced Concurrent MRU Map by 20001 down to: 100000.";

        LOG.info("dataText: " + dataText);

        PEGA0017ReportModel pega0017ReportModel = new PEGA0017ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0017ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("collections/mru/PropertyReference", alertMessageReportEntryKey);

    }
}
